package io.reciteapp.recite.csprofile;

import io.reciteapp.recite.csprofile.CsProfileContract.Model;
import io.reciteapp.recite.csprofile.CsProfileContract.Presenter;
import io.reciteapp.recite.csprofile.CsProfileContract.View;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.call.csprofile.CsProfileCall.GetCsProfileCallback;
import io.reciteapp.recite.data.model.CsProfileResponse;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class CsProfilePresenter implements Presenter {

  private NetworkCallWrapper service;
  private View view;
  private Model model;
  private CompositeSubscription subscriptions;

  public CsProfilePresenter(Model model) {
    this.model = model;
  }

  @Override
  public void setView(View view, NetworkCallWrapper service) {
    this.subscriptions = new CompositeSubscription();
    this.view = view;
    this.service = service;
  }

  @Override
  public void setId(String id) {
    model.setId(id);
  }

  /**
   * Check for view after a network call to avoid view null when result return from call
   * @return true for view is attach, false for view is null
   */
  private boolean isViewAttached() {
    return view != null;
  }

  @Override
  public void getCsProfile() {
    view.showWait();

    String token = model.getToken();
    String id = model.getId();
    Subscription subscription = service.getCsProfile(token, id, new GetCsProfileCallback() {
      @Override
      public void onSuccess(CsProfileResponse response) {
        if (isViewAttached()) {
          view.removeWait();
          view.setData(response);
        }
      }

      @Override
      public void onError(int responseCode) {
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
