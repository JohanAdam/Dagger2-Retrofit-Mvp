package io.reciteapp.recite.data.networking.call.surahlist;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.model.SurahListResponse;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import java.util.ArrayList;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Call for get Surah List
 */

public class SurahListCall {

  public interface GetSurahListCallback {
    void onSuccess(ArrayList<SurahListResponse> surahListResponses);
    void onError(NetworkError networkError);
  }

  public Subscription get(String token, NetworkService networkService, GetSurahListCallback callback) {
    return networkService.getSurahList(Constants.AZURE_VER, token)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Observable::error)
        .subscribe(new Subscriber<ArrayList<SurahListResponse>>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete getSurahList");
          }

          @Override
          public void onError(Throwable e) {
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
          public void onNext(ArrayList<SurahListResponse> surahListResponses) {
            callback.onSuccess(surahListResponses);
          }
        });
  }
}
