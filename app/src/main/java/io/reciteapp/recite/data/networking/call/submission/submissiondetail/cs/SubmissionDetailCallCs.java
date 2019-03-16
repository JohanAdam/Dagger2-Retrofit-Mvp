package io.reciteapp.recite.data.networking.call.submission.submissiondetail.cs;

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

public class SubmissionDetailCallCs {

  public interface GetSubmissionDetailCsCallback {
    void onSuccess(String result);
    void onError(NetworkError networkError);
  }

  public Subscription get(String token, String audioId, NetworkService networkService, GetSubmissionDetailCsCallback callback) {

    return networkService.getSubmissionDetailCs(Constants.ContentTypeValue,
        token,
        audioId)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<String>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete getSubmissionDetailCs");
          }

          @Override
          public void onError(Throwable e) {
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  Timber.d("onSuccess getRefreshToken");
                  get(newToken, audioId, networkService, callback);
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
          public void onNext(String s) {
            Timber.d("onNext SubmissionDetailCall %s", s);
            callback.onSuccess(s);
          }
        });

  }

}
