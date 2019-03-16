package io.reciteapp.recite.data.networking.call.recite;

import com.google.gson.Gson;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsSharedAccessSignature;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.model.AudioV2;
import io.reciteapp.recite.data.model.UploadFileResponse;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import java.io.IOException;
import java.net.URI;
import rx.Single;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AudioUploadCall {

  public Subscription uploadAudio(
      String token,
      AudioV2 audio,
      NetworkService networkService,
      UploadAudioUploadCallback callback) {
    Timber.d("uploadAudio");

    Gson gson = new Gson();
    String jsonAudio = gson.toJson(audio);

    Timber.d("jsonAudio %s", jsonAudio);

    // ---- GET SAS QUERY ----
    Timber.i("Get Sas Query");
    return networkService.getSasQueryUser(
        Constants.AZURE_VER,
        token,
        audio.getResourceName(),
        (int) audio.getAudioDuration())
        .flatMap(new Func1<UploadFileResponse, Single<Boolean>>() {
          @Override
          public Single<Boolean> call(UploadFileResponse uploadFileResponse) {
            //set new resource name
            audio.setResourceName(uploadFileResponse.getResourceName());

            // ---- UPLOAD AUDIO ----
            Timber.i("Upload Audio");
            return Single.just(uploadFileResponse.getSasQueryString())
                .flatMap(new Func1<String, Single<Boolean>>() {
                  @Override
                  public Single<Boolean> call(String sasQueryV) {
                    return doInBackground(sasQueryV,
                        audio.getAudioUri(),
                        uploadFileResponse.getAudioUri());
                  }
                });
          }
        })
        //---- UPLOAD AUDIO DATA ----
        .flatMap(new Func1<Boolean, Single<AudioV2>>() {
          @Override
          public Single<AudioV2> call(Boolean aBoolean) {
            if (aBoolean) {
              Timber.i("Upload Audio Data");
              return networkService.postAudioDataUser(
                  Constants.ContentTypeValue,
                  Constants.AZURE_VER,
                  token,
                  audio);
            } else {
              Timber.e("aBoolean is false");
              throw new RuntimeException();
            }
          }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<AudioV2>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete uploadAudio");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError uploadAudio");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  Timber.d("onSuccess getRefreshToken");
                  uploadAudio(newToken, audio, networkService, callback);
                }

                @Override
                public void onFailed(NetworkError networkError) {
                  Timber.e("onFailed getRefreshToken");
                  callback.onError(networkError);
                }
              });
            } else {
              callback.onError(new NetworkError(e));
            }
          }

          @Override
          public void onNext(AudioV2 audioV2) {
            Timber.d("onNext uploadAudio");
            callback.onSuccess(audioV2);
          }
        });

  }


  private Single<Boolean> doInBackground(String sasQuery, String localAudioUri, String onlineAudioUri) {
    Timber.d("doInBackground postAudioDataUser");
    Timber.d("sasQuery %s", sasQuery);
    Timber.d("audiouriLocal %s", localAudioUri);
    Timber.d("audioUriServer %s", onlineAudioUri);
    try {
      StorageCredentials credentials = new StorageCredentialsSharedAccessSignature(sasQuery);
      URI audioUri = URI.create(onlineAudioUri);

      URI localUri = URI.create(localAudioUri);
      CloudBlockBlob cloudBlockBlob = new CloudBlockBlob(audioUri, credentials);
      cloudBlockBlob.uploadFromFile(localUri.getPath());

      return Single.just(true);
    } catch (IOException | StorageException e) {
      e.printStackTrace();
      return Single.just(false);
    }
  }

  public interface UploadAudioUploadCallback {
    void onSuccess(AudioV2 item);
    void onError(NetworkError networkError);
  }

}
