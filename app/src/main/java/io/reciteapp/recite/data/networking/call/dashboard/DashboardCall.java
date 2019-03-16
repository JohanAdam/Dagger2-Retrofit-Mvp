package io.reciteapp.recite.data.networking.call.dashboard;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import io.reciteapp.recite.data.model.DashboardResponse;
import rx.Single;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class DashboardCall {

  public interface GetDashboardResponseCallback {
    void onSuccess(DashboardResponse dashboardResponse);
    void onError(NetworkError networkError);
  }

  public Subscription get(String token, NetworkService networkService, GetDashboardResponseCallback callback) {

    return networkService.getDashboard(token)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<DashboardResponse>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete getDashboard");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError getDashboard");

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
                  callback.onError(new NetworkError(e));
                }
              });
            } else {
              callback.onError(new NetworkError(e));
            }
          }

          @Override
          public void onNext(DashboardResponse dashboardResponse) {
            Timber.d("onNext getDashboard");
            callback.onSuccess(dashboardResponse);
          }
        });

  }

}
