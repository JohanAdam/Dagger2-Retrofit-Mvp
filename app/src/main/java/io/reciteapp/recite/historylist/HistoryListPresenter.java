package io.reciteapp.recite.historylist;

import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.call.history.historylist.HistoryListCall.GetHistoryListCallback;
import io.reciteapp.recite.historylist.HistoryListContract.Model;
import io.reciteapp.recite.historylist.HistoryListContract.Presenter;
import io.reciteapp.recite.historylist.HistoryListContract.View;
import io.reciteapp.recite.data.model.HistoryResponse;
import java.util.ArrayList;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class HistoryListPresenter implements Presenter {

  private NetworkCallWrapper service;
  private View view;
  private Model model;
  private CompositeSubscription subscriptions;

  /**
   * Initialized Presenter
   * @param model Set model to presenter
   */
  public HistoryListPresenter(Model model) {
    this.model = model;
  }

  /**
   * Set view and NetworkService in presenter
   * @param view HistoryList view
   */
  @Override
  public void setView(View view, NetworkCallWrapper service) {
    this.subscriptions = new CompositeSubscription();
    this.view = view;
    this.service = service;
  }

  @Override
  public void setSurahName(String surahName) {
    model.setSurahName(surahName);
  }

  @Override
  public void setSurahId(String surahId) {
    model.setSurahId(surahId);
  }

  /**
   * Check for view after a network call to avoid view null when result return from call
   * @return true for view is attach, false for view is null
   */
  private boolean isViewAttached() {
    return view != null;
  }

  @Override
  public void getHistoryList() {
    view.showWait();
    view.removeErrorLayout();
    view.removeNoResultLayout();

    String token = model.getToken();
    String surahId = model.getSurahId();
    Subscription subscription = service.getHistoryList(token, surahId,
        new GetHistoryListCallback() {
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
            if (isViewAttached()){
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
