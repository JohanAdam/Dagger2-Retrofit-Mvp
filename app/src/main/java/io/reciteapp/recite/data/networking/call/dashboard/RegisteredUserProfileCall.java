package io.reciteapp.recite.data.networking.call.dashboard;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import io.reciteapp.recite.data.model.RegisteredUserProfileResponse;
import rx.Single;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Call too registered user in table
 * this is needed for some group that need user to register before they entered the group
 */
public class RegisteredUserProfileCall {

  public interface PostRegisteredUserCallback{
    void onSuccess(RegisteredUserProfileResponse dataModel);
    void onError(int responseCode);
  }

  public Subscription post(String token, RegisteredUserProfileResponse dataModel, NetworkService networkService, PostRegisteredUserCallback callback) {
    return networkService.postRegisteredUserProfile(Constants.ContentTypeValue,
        Constants.AZURE_VER,
        token,
        dataModel)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<RegisteredUserProfileResponse>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete postRegisteredUser");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError RegisteredUserProfileCall post");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  Timber.d("onSuccess getRefreshToken");
                  post(newToken, dataModel, networkService, callback);
                }

                @Override
                public void onFailed(NetworkError networkError) {
                  Timber.e("onFailed getRefreshToken");
                  callback.onError(networkError.getResponseCode());
                }
              });
            } else {
              callback.onError(responseCode);
            }
          }

          @Override
          public void onNext(RegisteredUserProfileResponse registeredUserProfileResponse) {
            Timber.d("onNext RegisteredUserProfileCall post");
            callback.onSuccess(registeredUserProfileResponse);
          }
        });
  }

}
