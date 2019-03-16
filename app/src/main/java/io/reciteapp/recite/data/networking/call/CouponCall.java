package io.reciteapp.recite.data.networking.call;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import io.reciteapp.recite.data.model.CouponResponse;
import rx.Single;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CouponCall {

  public interface PostCouponCallback{
    void onSuccess(CouponResponse couponResponse);
    void onError(int ResponseCode);
  }

  public Subscription post(String token, String coupon, NetworkService service, PostCouponCallback callback) {

    return service.postCouponCode(Constants.AZURE_VER,
        token,
        coupon)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<CouponResponse>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete postCouponCode");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError postCouponCode");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  Timber.d("onSuccess getRefreshToken");
                  post(newToken, coupon, service, callback);
                }

                @Override
                public void onFailed(NetworkError networkError) {
                  Timber.e("onFailed getRefreshToken");
                  callback.onError(networkError.getResponseCode());
                }
              });
            } else {
              if (responseCode == Constants.RESPONSE_CODE_NOT_FOUND) {
                responseCode = Constants.RESPONSE_CODE_NOT_FOUND_CODE;
              }
              Timber.e("onError postCouponCode %s", responseCode);
              callback.onError(responseCode);
            }
          }


          @Override
          public void onNext(CouponResponse couponResponse) {
            Timber.d("onNext postCouponCode");
            callback.onSuccess(couponResponse);
          }
        });
  }

}
