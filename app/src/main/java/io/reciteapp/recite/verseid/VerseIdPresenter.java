package io.reciteapp.recite.verseid;

import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.call.verseid.VerseIdCall.VerseIdCallback;
import io.reciteapp.recite.data.model.QuranSearchResultResponse;
import io.reciteapp.recite.verseid.VerseIdContract.Model;
import io.reciteapp.recite.verseid.VerseIdContract.Presenter;
import io.reciteapp.recite.verseid.VerseIdContract.View;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class VerseIdPresenter implements Presenter {

  private NetworkCallWrapper service;
  private View view;
  private Model model;
  private CompositeSubscription subscriptions;

  public VerseIdPresenter(Model model) {
    this.model = model;
  }

  @Override
  public void setView(View view, NetworkCallWrapper service) {
    this.subscriptions = new CompositeSubscription();
    this.view = view;
    this.service = service;
  }

  /**
   * Check for view after a network call to avoid view null when result return from call
   * @return true for view is attach, false for view is null
   */
  private boolean isViewAttached() {
    return view != null;
  }


  @Override
  public void sendResult(String searchWord) {

    String token = model.getToken();
    Subscription subscription = service.getVerseId(token, searchWord, new VerseIdCallback() {
      @Override
      public void onSuccess(QuranSearchResultResponse resultResponse) {
        if (isViewAttached()) {
          view.removeWait();
          view.onSuccess(resultResponse);
        }
      }

      @Override
      public void onError(int response) {
        if (isViewAttached()) {
          view.removeWait();
          view.showErrorView(response);
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
