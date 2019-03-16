package io.reciteapp.recite.data.networking.call;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import io.reciteapp.recite.data.model.ReciteVersionResponse;
import java.util.ArrayList;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ReciteVersionCall {

  public interface GetReciteVersionCallback{
    void onSuccess(ArrayList<ReciteVersionResponse> reciteVersionResponse);
    void onError(int responseCode);
  }

  public Subscription get(NetworkService networkService, GetReciteVersionCallback callback) {
    return networkService.getReciteVersion(Constants.AZURE_VER)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Observable::error)
        .subscribe(new Subscriber<ArrayList<ReciteVersionResponse>>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete getReciteVersion");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError getReciteVersion");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  Timber.d("onSuccess getRefreshToken");
                  get(networkService, callback);
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
          public void onNext(ArrayList<ReciteVersionResponse> reciteVersionResponse) {
            Timber.d("onNext getReciteVersion");
            callback.onSuccess(reciteVersionResponse);
          }
        });
  }

}

