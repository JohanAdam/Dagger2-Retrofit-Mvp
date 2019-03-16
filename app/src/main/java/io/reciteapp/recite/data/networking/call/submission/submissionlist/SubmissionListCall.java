package io.reciteapp.recite.data.networking.call.submission.submissionlist;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.RefreshTokenCallback;
import io.reciteapp.recite.data.model.SubmissionListResponse;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkService;
import java.util.ArrayList;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SubmissionListCall {

  public interface GetSubmissionListCallback {
    void onSuccess(ArrayList<SubmissionListResponse> submissionListResponse);
    void onError(NetworkError networkError);
  }

  /**
   * Get submission list
   * token authentication token
   * orderList : ASC and DESC for sort list
   * takeValue : value to take in one list
   */
  public Subscription get(String token, String orderList, int takeValue, NetworkService networkService, GetSubmissionListCallback callback) {

    return networkService.getSubmissionList(Constants.AZURE_VER, token, orderList, takeValue)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Observable::error)
        .subscribe(new Subscriber<ArrayList<SubmissionListResponse>>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete getSubmissionList");
          }

          @Override
          public void onError(Throwable e) {
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  get(newToken, orderList, takeValue, networkService, callback);
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
          public void onNext(ArrayList<SubmissionListResponse> submissionListResponse) {
            callback.onSuccess(submissionListResponse);
          }
        });

  }

}
