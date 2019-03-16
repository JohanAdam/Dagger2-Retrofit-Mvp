package io.reciteapp.recite.data.networking.call.history.historylist;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import io.reciteapp.recite.data.model.HistoryResponse;
import java.util.ArrayList;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * History List
 */

public class HistoryListCall {

  public interface GetHistoryListCallback {
    void onSuccess(ArrayList<HistoryResponse> historyListResponse);
    void onError(NetworkError networkError);
  }

  public Subscription get(String token, String surahId, NetworkService networkService, GetHistoryListCallback callback) {

    return networkService.getHistoryList(Constants.AZURE_VER, token, surahId)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Observable::error)
        .subscribe(new Subscriber<ArrayList<HistoryResponse>>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete getHistoryList");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError getHistoryList");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  get(newToken, surahId, networkService, callback);
                }

                @Override
                public void onFailed(NetworkError networkError) {
                  callback.onError(networkError);
                }
              });
            } else {
              callback.onError(new NetworkError(e));
            }
          }

          @Override
          public void onNext(ArrayList<HistoryResponse> historyResponses) {
            Timber.d("onNext getHistoryList");
            callback.onSuccess(historyResponses);
          }
        });
  }

  public Subscription getAll(String token, NetworkService networkService, GetHistoryListCallback callback) {

    return networkService.getAllHistoryList(Constants.AZURE_VER, token)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Observable::error)
        .subscribe(new Subscriber<ArrayList<HistoryResponse>>() {
          @Override
          public void onCompleted() {
            Timber.d("onCompleted getAllHistory");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError getAllHistory");

            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  getAll(newToken, networkService, callback);
                }

                @Override
                public void onFailed(NetworkError networkError) {
                  callback.onError(networkError);
                }
              });
            } else {
              callback.onError(new NetworkError(e));
            }
          }

          @Override
          public void onNext(ArrayList<HistoryResponse> historyResponses) {
            Timber.d("onNext getAllHistory");
            callback.onSuccess(historyResponses);
          }
        });
  }
}
