package io.reciteapp.recite.share;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.call.StatsActionCall.GetStatsActionCallback;
import io.reciteapp.recite.data.networking.call.share.ShareCodeCall.GetShareCodeCallback;
import io.reciteapp.recite.share.ShareContract.Model;
import io.reciteapp.recite.share.ShareContract.Presenter;
import io.reciteapp.recite.share.ShareContract.View;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class SharePresenter implements Presenter {

  private NetworkCallWrapper service;
  private View view;
  private Model model;
  private CompositeSubscription subscriptions;

  public SharePresenter(Model model) {
    this.model = model;
  }

  @Override
  public void setView(View view, NetworkCallWrapper service) {
    this.subscriptions = new CompositeSubscription();
    this.view = view;
    this.service = service;
  }

  @Override
  public void checkLogin() {
    if (model.checkLogin()) {
      getReferralCode();
    } else {
      view.notLoginLayout();
    }
  }

  /**
   * Check for view after a network call to avoid view null when result return from call
   * @return true for view is attach, false for view is null
   */
  private boolean isViewAttached() {
    return view != null;
  }

  @Override
  public void getReferralCode() {
    Timber.d("getReferralCode");
    view.showWait();
    view.removeErrorLayout();

    Subscription subscription = service.getShareCode(model.getToken(), model.getUserId(), new GetShareCodeCallback() {
      @Override
      public void onSuccess(String code) {
        if (isViewAttached()) {
          view.removeWait();
          model.setShareCode(code);
          view.onSuccessLayout(code);
        }
      }

      @Override
      public void onFailure(NetworkError networkError) {
        if (isViewAttached()) {
          view.removeWait();
          view.showErrorLayout();
        }
      }
    });

    subscriptions.add(subscription);
  }

  @Override
  public void copyCode() {
    Timber.d("copyCode");
    if (isViewAttached()) {
      postActionStats(Constants.STATS_ACTION_COPY);

      model.copyShareCode();
      view.showToast(R.string.msg_copy_successfully);
    }
  }

  @Override
  public void shareCode() {
    Timber.d("shareCode");
    if (isViewAttached()) {
      postActionStats(Constants.STATS_ACTION_SHARE);

      view.shareCode(model.getShareCode());
    }
  }

  @Override
  public void postActionStats(String action) {
    Timber.d("postActionStats " + action);

    Subscription subscription = service.getStatsAction(model.getToken(), action,
        new GetStatsActionCallback() {
          @Override
          public void onSuccess() {
            Timber.d("onSuccess postActionStats");
          }

          @Override
          public void onFailure(NetworkError networkError) {
            Timber.d("onfailure postActionStats errorCode : " + networkError.getResponseCode());
          }
        });

    subscriptions.add(subscription);

  }

  @Override
  public void unSubscribe() {
    Timber.d("ubSubscribe");
    subscriptions.unsubscribe();
    view = null;
  }
}
