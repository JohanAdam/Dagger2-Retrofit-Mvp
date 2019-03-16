package io.reciteapp.recite.submissionlist.cs;

import io.reciteapp.recite.R;
import io.reciteapp.recite.data.model.SubmissionListResponse;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.call.submission.submissionlist.SubmissionListCall.GetSubmissionListCallback;
import io.reciteapp.recite.submissionlist.cs.SubmissionListCsContract.Model;
import io.reciteapp.recite.submissionlist.cs.SubmissionListCsContract.Presenter;
import io.reciteapp.recite.submissionlist.cs.SubmissionListCsContract.View;
import java.util.ArrayList;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class SubmissionListCsPresenter implements Presenter {

  private NetworkCallWrapper service;
  private View view;
  private Model model;
  private CompositeSubscription subscriptions;
  private static int takeValue = 20;

  /**
   * Initialized Model
   * @param model model pass from inject in fragement
   */
  public SubmissionListCsPresenter(Model model) {
    this.model = model;
  }

  /**
   * Set view and service from View
   */
  @Override
  public void setView(View view, NetworkCallWrapper service) {
    this.subscriptions = new CompositeSubscription();
    this.view = view;
    this.service = service;
  }

  /**
   * Save sort pref to sharedPref
   * Get submission list
   * Set the view of the sort
   * @param orderListSort 0 = Newest, 1 = Oldest;
   */
  @Override
  public void setSortPreferences(int orderListSort) {
    Timber.d("setSortPreferences orderListSort is %s", orderListSort);
    model.saveSortPref(orderListSort);
    setSortPrefToView(orderListSort);
  }

  /**
   * Load sort pref state from sharedPref
   * Get submission List
   * Set the view of the sort
   */
  @Override
  public void getSortPreferences() {
    int orderListSort = model.loadSortPref();
    Timber.d("getSortPreferences orderListSort is %s", orderListSort);
    setSortPrefToView(orderListSort);
  }

  /**
   * Set the sort pref to the vie
   * @param orderList 0 = Ascending ; 1 = Descending ;
   */
  private void setSortPrefToView(int orderList) {
    Timber.d("setSortPrefToView");
    getSubmissionList(orderList, takeValue);

    switch (orderList) {
      case 0:
        view.setSortText(R.string.title_newest);
        break;
      case 1:
        view.setSortText(R.string.title_oldest);
        break;
    }
  }

  /**
   * Return the if the value still attached
   * @return true = still attached ; false = not attached
   */
  private boolean isViewAttached() {
    return view != null;
  }

  /**
   * Get submission list
   * @param orderList Sort orderListSort by
   * 0 = Asc ; 1 = Desc ;
   * @param listTakeValue , value how much surah to request in one request.
   */
  private void getSubmissionList(int orderList, int listTakeValue) {
    view.showWait();
    view.removeEmptyLayout();
    view.removeErrorLayout();
    view.removeSubmissionList();

    String orderListValue = ((orderList == 0) ? "ASC" : "DESC");

    String token = model.getToken();
    Subscription subscription = service.getSubmissionList(token, orderListValue, listTakeValue,
        new GetSubmissionListCallback() {
          @Override
          public void onSuccess(ArrayList<SubmissionListResponse> submissionResponse) {
            if (isViewAttached()) {
              view.removeWait();

              if (submissionResponse.size() == 0) {
                view.showEmptyLayout();
              } else {
                view.setSubmissionListoRv(submissionResponse);
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
   * Unsubscribe from all call and set the view to null
   */
  @Override
  public void unSubscribe() {
    subscriptions.unsubscribe();
    view = null;
  }
}
