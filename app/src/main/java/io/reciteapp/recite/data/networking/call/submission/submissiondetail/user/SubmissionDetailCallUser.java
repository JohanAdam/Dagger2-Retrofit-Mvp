package io.reciteapp.recite.data.networking.call.submission.submissiondetail.user;

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

public class SubmissionDetailCallUser {

  public interface GetSubmissionDetailUserCallback {
    void onSuccess(HistoryDetailResponse submissionDetailResponse);
    void onError(NetworkError networkError);
  }

  public interface PostAudioSubmissionRatingCallback {
    void onSuccess();
    void onError(NetworkError networkError);
  }

  public Subscription get(String token, String audioId, NetworkService networkService, GetSubmissionDetailUserCallback callback) {

    return networkService.getSubmissionDetailUser(Constants.AZURE_VER, token, audioId)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<HistoryDetailResponse>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete getSubmissionDetailUser");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError getSubmissionDetailUser");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  get(newToken, audioId, networkService, callback);
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
          public void onNext(HistoryDetailResponse historyDetailResponse) {
            Timber.d("onNext getSubmissionDetailUser");
            callback.onSuccess(historyDetailResponse);
          }
        });

  }


  public Subscription postRating(String token, String audioId, String rating, NetworkService networkService, PostAudioSubmissionRatingCallback callback) {

    return networkService.postSubmissionRating(Constants.AZURE_VER, token, audioId, rating)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(Single::error)
        .subscribe(new Subscriber<String>() {
          @Override
          public void onCompleted() {
            Timber.d("onComplete postRating");
          }

          @Override
          public void onError(Throwable e) {
            Timber.e("onError postRating");
            int responseCode = new NetworkError(e).getResponseCode();

            if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
              new AuthenticationRequest().getRefreshToken(new RefreshTokenCallback() {
                @Override
                public void onSuccess(String newToken) {
                  postRating(newToken, audioId, rating, networkService, callback);
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
          public void onNext(String s) {
            Timber.d("onNext postRating");
            callback.onSuccess();
          }
        });

  }
}
