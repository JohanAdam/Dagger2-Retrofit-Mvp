package io.reciteapp.recite.data.networking.call.submission.submissiondetail.cs;

import com.google.gson.Gson;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsSharedAccessSignature;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.model.UploadFileResponse;
import io.reciteapp.recite.data.model.UstazReviewCommentResponse;
import io.reciteapp.recite.data.model.UstazReviewResponse;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CsReviewRepository {

  public Subscription uploadCsReview(
      String token,
      UstazReviewResponse ustazReviewResponse,
      String resourceName,
      NetworkService networkService,
      CsReviewRepoCallback callback) {

    return Observable.from(ustazReviewResponse.getUstazReviewComments())
        .concatMap(
            (Func1<UstazReviewCommentResponse, Observable<UstazReviewCommentResponse>>) ustazReviewCommentResponse -> {

              // ---- CHECK FOR AVAILABLE COMMENT AUDIO ----
              if (ustazReviewCommentResponse.isAttachmentAvailable()) {
                //Audio Available
                Timber.i("Audio available ");

                String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

                //Suggest resourceName for server
                ustazReviewCommentResponse.setResourceName(resourceName
                    + "attachment"
                    + timeStamp
                    + ustazReviewCommentResponse.getAudioDuration().replace(":", ""));

                // ---- GET SAS QUERY ----
                Timber.i("Get SAS Query");
                return networkService.getSasQueryCs(token, ustazReviewCommentResponse.getResourceName())
                    .flatMap(
                        (Func1<UploadFileResponse, Observable<Boolean>>) uploadFileResponse -> {
                          //Set server approved resources name
                          ustazReviewCommentResponse.setResourceName(uploadFileResponse.getResourceName());

                          // ---- UPLOAD AUDIO COMMENT ----
                          Timber.i("Upload Audio");
                          return Observable.just(uploadFileResponse.getSasQueryString())
                              .flatMap(new Func1<String, Observable<Boolean>>() {
                                @Override
                                public Observable<Boolean> call(String sasQueryV) {
                                  return CsReviewRepository.this.doInBackground(
                                      sasQueryV,
                                      ustazReviewCommentResponse.getAudioUri(),
                                      uploadFileResponse.getAudioUri());
                                }
                              });

                        })
                    .flatMap((Func1<Boolean, Observable<UstazReviewCommentResponse>>) (Boolean aBoolean) -> {
                      if (aBoolean) {
                        Timber.i("Success upload audio");
                        return Observable.just(ustazReviewCommentResponse);
                      } else {
                        Timber.e("Failed upload audio");
                        throw new RuntimeException();
                      }
                    });
              } else {
                //Audio not available
                Timber.i("Audio not available");
                ustazReviewCommentResponse.setResourceName(null);
                return Observable.just(ustazReviewCommentResponse);
              }
            })
        .toList()
        .flatMap(
            (Func1<List<UstazReviewCommentResponse>, Observable<UstazReviewResponse>>) commentResponseList -> {
              // ---- UPLOAD THE AUDIO DATA ----
              Gson gson = new Gson();
              String jsonResult = gson.toJson(commentResponseList);
              Timber.i("Upload UstazReview data %s", jsonResult);

              // Set the new list return by server.
              ustazReviewResponse.setUstazReviewComments(commentResponseList);

              return networkService.postUstazReviewData(
                  Constants.ContentTypeValue,
                  token,
                  ustazReviewResponse);
            })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Observable::error)
        .subscribe(new Subscriber<UstazReviewResponse>() {
          @Override
          public void onCompleted() {
            Timber.d("onCompleted uploadCsReview");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError uploadCsReview");
            e.printStackTrace();
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  uploadCsReview(newToken, ustazReviewResponse, resourceName, networkService, callback);
                }

                @Override
                public void onFailed(NetworkError networkError) {
                  callback.onError(networkError);
                }
              });
            } else {
              callback.onError(new NetworkError(e));
            }
          }

          @Override
          public void onNext(UstazReviewResponse result) {
            Gson gson = new Gson();
            String jsonResult = gson.toJson(result);
            Timber.i("onNext %s" , jsonResult);
            callback.onSuccess(result);
          }
        });
  }


  //Main function of postAudioAttachmentService
  private Observable<Boolean> doInBackground(String sasQuery,
      String audioCommentUriLocal,
      String audioCommentUriServer){
    Timber.d("doInBackground audio comment upload");
    Timber.d("sasQuery %s", sasQuery);
    Timber.d("audioUriLocal %s", audioCommentUriLocal);
    Timber.d("audioUriServer %s", audioCommentUriServer);
    try {
      StorageCredentials credentials = new StorageCredentialsSharedAccessSignature(sasQuery);
      URI audioUri = URI.create(audioCommentUriServer);

      URI localUri = URI.create(audioCommentUriLocal);
      CloudBlockBlob cloudBlockBlob = new CloudBlockBlob(audioUri, credentials);
      cloudBlockBlob.uploadFromFile(localUri.getPath());

      return Observable.just(true);
    } catch (IOException | StorageException e) {
      e.printStackTrace();
      return Observable.just(false);
    }
  }

  public interface CsReviewRepoCallback {
    void onSuccess(UstazReviewResponse ustazReviewResponse);
    void onError(NetworkError networkError);
  }

}
