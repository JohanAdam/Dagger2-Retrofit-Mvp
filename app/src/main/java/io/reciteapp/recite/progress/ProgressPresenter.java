package io.reciteapp.recite.progress;

import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.call.history.historylist.HistoryListCall.GetHistoryListCallback;
import io.reciteapp.recite.data.model.HistoryResponse;
import io.reciteapp.recite.progress.ProgressContract.Model;
import io.reciteapp.recite.progress.ProgressContract.Presenter;
import io.reciteapp.recite.progress.ProgressContract.View;
import java.util.ArrayList;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class ProgressPresenter implements Presenter {

  private NetworkCallWrapper service;
  private View view;
  private Model model;
  private CompositeSubscription subscriptions;

  public ProgressPresenter(Model model) {
    this.model = model;
  }

  @Override
  public void setView(View view, NetworkCallWrapper service) {
    this.subscriptions = new CompositeSubscription();
    this.view = view;
    this.service = service;
  }

  @Override
  public boolean getLoginStatus() {
    return model.getLoginStatus();
  }

  private boolean isViewAttached() {
    return view != null;
  }

  @Override
  public void getHistoryList() {
    view.showWait();
    view.removeErrorLayout();
    view.removeNoResultLayout();

    String token = model.getToken();
    Subscription subscription = service.getHistoryList(token, null, new GetHistoryListCallback() {
      @Override
      public void onSuccess(ArrayList<HistoryResponse> historyListResponse) {
        if (isViewAttached()) {
          view.removeWait();
          if (historyListResponse.size() > 0) {
            view.setHistoryListToRv(historyListResponse);
          } else {
            view.showNoResultLayout();
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

  @Override
  public void unSubscribe() {
    subscriptions.unsubscribe();
    view = null;
  }
}
