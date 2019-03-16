package io.reciteapp.recite.data.networking.call.history.historydetail;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import io.reciteapp.recite.data.model.HistoryDetailResponse;
import rx.Single;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Get history detail
 */

public class HistoryDetailCall {

  public interface GetHistoryDetailCallback {
    void onSuccess(HistoryDetailResponse historyDetailResponse);
    void onError(NetworkError networkError);
  }

  public Subscription get(String token, String audioId, NetworkService networkService, GetHistoryDetailCallback callback) {

    return networkService.getHistoryDetail(
        Constants.AZURE_VER, token, audioId)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<HistoryDetailResponse>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete getHistoryDetails");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError HistoryDetailCall get");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  Timber.d("onSuccess getRefreshToken");
                  get(newToken, audioId, networkService, callback);
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
          public void onNext(HistoryDetailResponse historyDetailResponse) {
            Timber.d("onNext HistoryDetailCall get");
            callback.onSuccess(historyDetailResponse);
          }
        });

  }

}
