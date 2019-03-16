package io.reciteapp.recite.data.networking.call.submission.submissiondetail;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import rx.Single;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ReportAudioCall {

  public interface PostReportCallback {
    void onSuccess();
    void onError(NetworkError networkError);
  }

  public Subscription post(String token, String id, String text, NetworkService networkService, PostReportCallback callback) {

    return networkService.postReport(
        Constants.AZURE_VER,
        token,
        id,
        text)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<Void>() {
          @Override
          public void onCompleted() {
          Timber.d("onComplete postReportAudio");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError postReportAudio");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  post(newToken, id, text, networkService, callback);
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
          public void onNext(Void aVoid) {
            Timber.d("onNext postReportAudio");
            callback.onSuccess();
          }
        });

  }

}
