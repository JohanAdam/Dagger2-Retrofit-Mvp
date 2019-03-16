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

public class NotificationTokenCall {

  public interface PostNotificationCallback {
    void onSuccess();
    void onError(int response);
  }

  public Subscription post(String token, String notificationToken, NetworkService networkService, PostNotificationCallback callback) {

    return networkService.postNotificationToken(token, notificationToken)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<Void>() {
          @Override
          public void onCompleted() {
            Timber.d("onCompleted postNotificationCall");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError postNotificationCall");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  Timber.d("onSuccess getRefreshToken");
                  post(newToken, notificationToken, networkService, callback);
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
          public void onNext(Void aVoid) {
            Timber.d("onNext postNotificationCall");
            callback.onSuccess();
          }
        });

  }

}
