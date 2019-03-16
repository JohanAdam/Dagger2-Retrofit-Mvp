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

public class ReferralCall {

  public interface PatchReferralCodeCallback {
    void onSuccess(String result);
    void onError(int responseCode);
  }

  public Subscription patch(String token, String refCode, NetworkService networkService, PatchReferralCodeCallback callback) {
    return networkService.patchReferralCode(Constants.AZURE_VER, token, refCode)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<String>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete patchReferralCode");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError patchReferralCode");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  Timber.d("onSuccess getRefreshToken");
                  patch(newToken, refCode, networkService, callback);
                }

                @Override
                public void onFailed(NetworkError networkError) {
                  Timber.e("onFailed getRefreshToken");
                  callback.onError(networkError.getResponseCode());
                }
              });
            } else {
              if (new NetworkError(e).getResponseCode() == Constants.RESPONSE_CODE_NOT_FOUND) {
                e.printStackTrace();
                responseCode = Constants.RESPONSE_CODE_NOT_FOUND_CODE;
              }
              callback.onError(responseCode);
            }
          }

          @Override
          public void onNext(String s) {
            callback.onSuccess(s);
          }
        });
  }

}
