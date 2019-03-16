package io.reciteapp.recite.submissionlist.user;

import android.os.CountDownTimer;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.call.submission.submissionlist.SubmissionListCall.GetSubmissionListCallback;
import io.reciteapp.recite.data.model.SubmissionListResponse;
import io.reciteapp.recite.submissionlist.user.SubmissionListContract.Model;
import io.reciteapp.recite.submissionlist.user.SubmissionListContract.Presenter;
import io.reciteapp.recite.submissionlist.user.SubmissionListContract.View;
import java.util.ArrayList;
import java.util.List;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class SubmissionListPresenter implements Presenter{

  private NetworkCallWrapper service;
  private View view;
  private Model model;
  private CompositeSubscription subscriptions;
  private CountDownTimer timer;

  public SubmissionListPresenter(Model model) {
    this.model = model;
  }

  @Override
  public void setView(View view, NetworkCallWrapper service) {
    this.subscriptions = new CompositeSubscription();
    this.view = view;
    this.service = service;
  }

  /**
   * Return true if view still available, otherwise false
   */
  private boolean isViewAttached() {
    return view != null;
  }

  /**
   * Get submission list from server
   */
  @Override
  public void getSubmissionList() {
    Timber.d("getSubmissionList");
    view.showWait();
    view.removeErrorLayout();
    view.removeNoResultLayout();
    view.removeSubmissionList();

    //order not give any effect to normal user.
    String token = model.getToken();
    Subscription subscription = service.getSubmissionList(token, "ASC" , 20,
        new GetSubmissionListCallback() {
          @Override
          public void onSuccess(ArrayList<SubmissionListResponse> submissionResponse) {
            if (isViewAttached()) {
              view.removeWait();
              if (submissionResponse.size() == 0) {
                view.showNoResultLayout();
              } else {
                processSubmissionList(submissionResponse);
              }
            }
          }

          @Override
          public void onError(NetworkError networkError) {
            if (isViewAttached()) {
              view.removeWait();
              view.showErrorLayout();
            }
          }
        });

    subscriptions.add(subscription);
  }

  /**
   * Filter list
   * @param query key that user key in to search for surah
   */
  @Override
  public void filteredList(String query) {
    if (isViewAttached()) {
      view.removeErrorLayout();
      view.removeNoResultLayout();
      view.removeSubmissionList();

      List<SubmissionListResponse> submissionList = model.getSubmissionList();

      query = query.toLowerCase().trim();

      final ArrayList<SubmissionListResponse> filteredModelList = new ArrayList<>();

      if (submissionList != null && !submissionList.isEmpty()) {
        for (SubmissionListResponse data : submissionList) {
          String surahName = data.getSurahName().toLowerCase().trim();
          if (surahName.contains(query)) {
            filteredModelList.add(data);
          }
        }

        if (!filteredModelList.isEmpty()) {
          view.setSubmissionListToRv(filteredModelList);
        } else {
          view.showNoResultLayout();
        }
      } else {
        view.removeNoResultLayout();
      }

    }
  }

  /**
   * Setup submission list response get from server
   */
  private void processSubmissionList(ArrayList<SubmissionListResponse> response) {

    try {
      if (response.size() > 0) {
        model.setSubmissionList(response);
        view.setSubmissionListToRv(response);
      } else {
        view.showNoResultLayout();
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
      view.showNoResultLayout();
      view.removeErrorLayout();
    }
  }

  /**
   * Cancel all network call
   * Cancel timer if running
   */
  @Override
  public void unSubscribe() {
    subscriptions.unsubscribe();
    if (timer != null) {
      timer.cancel();
    }
    view = null;
  }
}
