package io.reciteapp.recite.data.networking.call.reload;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import io.reciteapp.recite.data.model.PaymentResponse;
import rx.Single;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PaymentCall {

  public interface GetPaymentCallback {
    void onSuccess(PaymentResponse paymentResponse);
    void onError(NetworkError networkError);
  }

  public Subscription get(String token, String numberPhone, String provider, String packageId, NetworkService networkService, GetPaymentCallback callback) {

    return networkService.getPayment(token, provider, numberPhone, packageId)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<PaymentResponse>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete getPayment");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError getPayment");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  Timber.d("onSuccess getRefreshToken");
                  get(newToken, numberPhone, provider, packageId, networkService, callback);
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
          public void onNext(PaymentResponse paymentResponse) {
            Timber.d("onNext getPayment");
            callback.onSuccess(paymentResponse);
          }
        });

  }

}
