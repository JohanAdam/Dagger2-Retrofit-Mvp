package io.reciteapp.recite.data.networking.call.verseid;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import io.reciteapp.recite.data.model.QuranSearchResultResponse;
import rx.Single;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class VerseIdCall {

  public interface VerseIdCallback {
    void onSuccess(QuranSearchResultResponse resultResponse);
    void onError(int response);
  }

  public Subscription get(String token, String searchWord, NetworkService networkService, VerseIdCallback callback) {

    return networkService.getVerseId(token, searchWord)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<QuranSearchResultResponse>() {
          @Override
          public void onCompleted() {
            Timber.d("onCompleted getVerseIdCall");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError getVerseIdCall");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  Timber.d("onSuccess getRefreshToken");
                  get(newToken, searchWord, networkService, callback);
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
          public void onNext(QuranSearchResultResponse resultResponse) {
            Timber.d("onNext getVerseIdCall");
            callback.onSuccess(resultResponse);
          }
        });

  }

}
