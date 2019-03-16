package io.reciteapp.recite.data.networking.call.reload;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import io.reciteapp.recite.data.model.PackageResponse;
import rx.Single;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ReloadCall {

  /**
   * Get reload package list
   */

  public interface GetReloadCallback {
    void onSuccess(PackageResponse packageResponses);
    void onError(NetworkError networkError);
  }

  public Subscription get(String token, NetworkService networkService, GetReloadCallback callback) {

    return networkService.getReloadPackage(token)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<PackageResponse>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete getReloadCall");
          }

          @Override
          public void onError(Throwable e){
            Timber.e("onError getReloadCall");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  Timber.d("onSuccess getRefreshToken");
                  get(newToken, networkService, callback);
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
          public void onNext(PackageResponse packageResponse) {
            callback.onSuccess(packageResponse);
          }
        });
  }

}
