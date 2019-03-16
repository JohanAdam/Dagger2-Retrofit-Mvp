package io.reciteapp.recite.data.networking.call.share;

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

public class ShareCodeCall {

  public interface GetShareCodeCallback {
    void onSuccess(String code);
    void onFailure(NetworkError networkError);
  }

  public Subscription get(String token, String userId, NetworkService networkService, GetShareCodeCallback callback) {
    return networkService.getReferralCode(token, userId)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<String>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete getShareCode");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError getShareCode");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  Timber.d("onSuccess getRefreshToken");
                  get(newToken, userId, networkService, callback);
                }

                @Override
                public void onFailed(NetworkError networkError) {
                  Timber.e("onFailed getRefreshToken");
                  callback.onFailure(networkError);
                }
              });
            } else {
              callback.onFailure(new NetworkError(e));
            }
          }

          @Override
          public void onNext(String shareCode) {
            callback.onSuccess(shareCode);
          }
        });
  }

}
