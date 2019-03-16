package io.reciteapp.recite.data.networking.call.dashboard;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import io.reciteapp.recite.data.model.FreeCreditHistory;
import rx.Single;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Call when user claim the free credit
 */
public class FreeCreditHistoryCall {

  public interface PatchFreeCreditHistory{
    void onSuccess();
    void onError(int responseCode);
  }

  public Subscription patch(String token,
      boolean isAccept,
      String freeCreditHistoryId,
      NetworkService networkService,
      PatchFreeCreditHistory callback) {

    //1 is true/Accept , 2 is false/Cancel
    int isAcceptResult;
    if (isAccept) {
      isAcceptResult = 1;
    } else {
      isAcceptResult = 2;
    }

    FreeCreditHistory body = new FreeCreditHistory();
    body.setId(freeCreditHistoryId);
    body.setIsAccept(isAcceptResult);

    return networkService.patchFreeCreditHistory(Constants.ContentTypeValue,
        Constants.AZURE_VER,
        token,
        freeCreditHistoryId,
        isAcceptResult)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<Void>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete patchFreeCreditHistory");
          }

          @Override
          public void onError(Throwable e) {
            Timber.d("onError FreeCreditHistoryCall patch");

            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  Timber.d("onSuccess getRefreshToken");
                  patch(newToken, isAccept, freeCreditHistoryId, networkService, callback);
                }

                @Override
                public void onFailed(NetworkError networkError) {
                  Timber.e("onFailed getRefreshToken");
                  callback.onError(networkError.getResponseCode());
                }
              });
            } else {
              if (responseCode == 400) {
                //change to credit not found
                responseCode = Constants.RESPONSE_CODE_CREDIT_NOT_FOUND;
              } else {
                responseCode = new NetworkError(e).getResponseCode();
              }
              callback.onError(responseCode);
            }
          }

          @Override
          public void onNext(Void aVoid) {
            Timber.d("onNext FreeCreditHistoryCall patch");
            callback.onSuccess();
          }
        });
  }

}
