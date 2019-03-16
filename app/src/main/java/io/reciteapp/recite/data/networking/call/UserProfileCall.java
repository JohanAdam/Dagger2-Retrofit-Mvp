package io.reciteapp.recite.data.networking.call;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import io.reciteapp.recite.data.model.UserProfileResponse;
import java.util.ArrayList;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class UserProfileCall {

  public interface GetUserProfileResponseCallback {
    void onSuccess(ArrayList<UserProfileResponse> userProfileResponse);
    void onError(int responseCode);
  }

  public Subscription get(String token, NetworkService networkService, GetUserProfileResponseCallback callback) {
    return networkService.getUserProfile(Constants.ContentTypeValue, Constants.AZURE_VER, token)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Observable::error)
        .subscribe(new Subscriber<ArrayList<UserProfileResponse>>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete getUserProfile");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError getUserProfile");
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
                  callback.onError(networkError.getResponseCode());
                }
              });
            } else {
              callback.onError(responseCode);
            }
          }

          @Override
          public void onNext(ArrayList<UserProfileResponse> userProfileResponses) {
            callback.onSuccess(userProfileResponses);
          }
        });
  }

}
