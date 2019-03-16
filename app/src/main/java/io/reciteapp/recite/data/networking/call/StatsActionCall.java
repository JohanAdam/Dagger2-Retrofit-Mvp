package io.reciteapp.recite.data.networking.call;

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

public class StatsActionCall {

  public interface GetStatsActionCallback {
    void onSuccess();
    void onFailure(NetworkError networkError);
  }

  public Subscription get(String token, String action, NetworkService networkService, GetStatsActionCallback callback) {
    return networkService.getStats(token, action)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<Void>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete getStats");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError getStats");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  Timber.d("onSuccess getRefreshToken");
                  get(newToken, action, networkService, callback);
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
          public void onNext(Void aVoid) {
            callback.onSuccess();
          }
        });
  }

}
