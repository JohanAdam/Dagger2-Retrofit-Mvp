package io.reciteapp.recite.data.networking.call.recite;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import io.reciteapp.recite.data.model.SubmissionRecorderResponse;
import rx.Single;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ReciteTimeCall {

  public interface GetReciteTimeCallback {
    void onSuccess(SubmissionRecorderResponse recordPageResponse);
    void onError(NetworkError networkError);
  }

  public Subscription get(String token,
      String surahId,
      NetworkService networkService,
      GetReciteTimeCallback callback) {

    return networkService.getReciteTime(Constants.AZURE_VER, token, surahId)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<SubmissionRecorderResponse>() {
          @Override
          public void onCompleted() {
            Timber.d("onCompleted getReciteTime");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError getReciteTime");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  Timber.d("onSuccess getRefreshToken");
                  get(newToken, surahId, networkService, callback);
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
          public void onNext(SubmissionRecorderResponse recordPageResponse) {
            Timber.d("onNext getReciteTime");
            callback.onSuccess(recordPageResponse);
          }
        });
  }

}
