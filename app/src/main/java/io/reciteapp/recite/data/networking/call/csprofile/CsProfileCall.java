package io.reciteapp.recite.data.networking.call.csprofile;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import io.reciteapp.recite.data.model.CsProfileResponse;
import rx.Single;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CsProfileCall {

  public interface GetCsProfileCallback {
    void onSuccess(CsProfileResponse response);
    void onError(int responseCode);
  }

  public Subscription get(String token, String id, NetworkService networkService, GetCsProfileCallback callback) {

    return networkService.getCsProfile(
        Constants.AZURE_VER,
        token,
        id)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<CsProfileResponse>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete getCsprofile");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError getCsProfile");
            int responseCode = new NetworkError(e).getResponseCode();

            if (new NetworkError(e).getResponseCode() == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  get(newToken, id, networkService, callback);
                }

                @Override
                public void onFailed(NetworkError networkError) {
                  callback.onError(networkError.getResponseCode());
                }
              });
            } else {
              callback.onError(responseCode);
            }
          }

          @Override
          public void onNext(CsProfileResponse response) {
            Timber.d("onNext getCsProfile");
            callback.onSuccess(response);
          }
        });

  }

}
