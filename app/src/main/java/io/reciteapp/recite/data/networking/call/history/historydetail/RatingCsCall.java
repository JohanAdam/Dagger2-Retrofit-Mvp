package io.reciteapp.recite.data.networking.call.history.historydetail;

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

/**
 * To patch rating cs in cs profile
 */

public class RatingCsCall {

  public interface PatchRatingCsCallback {
    void onSuccess();
    void onError(int responseCode);
  }

  public Subscription patch(String token, String id, String feedback, String rating, NetworkService networkService, PatchRatingCsCallback callback) {
    
    return networkService.patchRatingCs(Constants.AZURE_VER,
        token,
        id,
        feedback,
        rating)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<String>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete patchRatingCs");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError patchRatingCs");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  Timber.d("onSuccess getRefreshToken");
                  patch(newToken, id, feedback, rating, networkService, callback);
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
          public void onNext(String s) {
            Timber.d("onNext patchRatingCs");
            callback.onSuccess();
          }
        });
  }

}
