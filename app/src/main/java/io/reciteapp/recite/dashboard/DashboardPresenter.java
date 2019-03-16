package io.reciteapp.recite.dashboard;

import android.text.TextUtils;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.dashboard.DashboardContract.Model;
import io.reciteapp.recite.dashboard.DashboardContract.View;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.call.dashboard.DashboardCall.GetDashboardResponseCallback;
import io.reciteapp.recite.data.networking.call.dashboard.EmailCall.PatchEmailCallback;
import io.reciteapp.recite.data.model.DashboardResponse;
import io.reciteapp.recite.data.model.RegisteredUserProfileResponse;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * DashboardCall presenter
 */

public class DashboardPresenter implements DashboardContract.Presenter{

  private NetworkCallWrapper service;
  private View view;
  private DashboardContract.Model model;
  private CompositeSubscription subscriptions;

  //Init presenter
  public DashboardPresenter(Model model) {
    this.model = model;
  }

  @Override
  public void setView(View view, NetworkCallWrapper service) {
    Timber.d("setView");
    this.view = view;
    this.service = service;
    this.subscriptions = new CompositeSubscription();
  }

  /**
   * For testing purpose
   * @return authentication token
   */
  @Override
  public String getToken() {
    return model.getToken();
  }

  /**
   * Check for view after a network call to avoid view null when result return from call
   * @return true for view is attach, false for view is null
   */
  private boolean isViewAttached() {
    return view != null;
  }

  @Override
  public boolean getLoginStatus() {
    return model.getLoginStatus();
  }

  /**
   * Use in onResume to check pending notification.
   * Check if there is message from Recite
   * Check if there is notification from referral programme
   */
  @Override
  public void checkNotification() {
    String message = model.getMessage();
    if (!TextUtils.isEmpty(message)) {
      Timber.d("Message available");
      view.showInfoDialog("Recite", message);
      model.clearData(Constants.PREF_MESSAGE);
    }

    int value = model.getReferralTime();
    Timber.d("value of referralTime in millisec %s", value);
    if (value != 0) {
      view.showShareDialog(value);
      model.clearData(Constants.PREF_REFERRAL_TIME);
    }
  }

  @Override
  public void getDashboard() {
//    this.service = service;
    Timber.d("getDashboard");
    view.showWait();
    String token =  model.getToken();
    Subscription subscription = service.getDashboard(token, new GetDashboardResponseCallback() {

      @Override
      public void onSuccess(DashboardResponse dashboardResponse) {
        model.setDashboardData(dashboardResponse);
        if (isViewAttached()) {
          view.removeWait();
          successViewSetup(dashboardResponse);
        }
      }

      @Override
      public void onError(NetworkError networkError) {
        if (isViewAttached()) {
          view.removeWait();
          view.notLoginView();
          view.showSnackBar(networkError.getAppErrorMessageResources());
        }
      }

    });
    subscriptions.add(subscription);

  }

  @Override
  public void openSurahList() {
    if (model.getLoginStatus()) {
      if (model.getDashboardData() != null) {
        if (model.getDashboardData().isDisableRecital()) {
          //Quota Reached , show error
          view.showInfoDialog(R.string.title_sorry , R.string.msg_quota_reach);
        } else {
          view.openSurahList();
        }
      } else {
        view.openSurahList();
      }
    } else {
      view.openLoginActivity();
    }
  }

  @Override
  public void openSubmissionList() {
    if (model.getLoginStatus()) {
      if (model.getCsState()) {
        view.openSubmissionListCs();
      } else {
        view.openSubmissionList();
      }
    } else {
      view.openLoginActivity();
    }
  }

  /**
   * Unused in new Recite Server.
   * @param dataModel model of User info for registration.
   * @param checkRequiredPayment check if user need to pay or not.
   * @param billPlzPackageID billplz package id needed for payment.
   */
  @Override
  public void postRegister(RegisteredUserProfileResponse dataModel, boolean checkRequiredPayment,
      String billPlzPackageID) {
  }

  @Override
  public void patchEmail(String email) {
    view.showLoadingDialog();

    String token = model.getToken();
    Subscription subscription = service.patchEmail(token, email, new PatchEmailCallback() {
      @Override
      public void onSuccess() {
        if (isViewAttached()) {
          view.removeLoadingDialog();
          //save email
          model.setEmail(email);
          //refresh
          getDashboard();
        }
      }

      @Override
      public void onError(int responseCode) {
        if (isViewAttached()) {
          view.removeLoadingDialog();
          view.showErrorDialog(responseCode, false);
        }
      }
    });

    subscriptions.add(subscription);
  }

  /**
   * Unused in new Recite Server.
   * @param isAccept boolean for flagging if user press ok or cancel.
   * @param freeCreditHistoryId id of ReciteTime credit.
   */
  @Override
  public void patchFreeCreditHistory(boolean isAccept, String freeCreditHistoryId) {

  }

  private void successViewSetup(DashboardResponse response) {

    //set Last Seen and Total Submission Info
    //Last Recite being sent by server in seconds, convert to hours.
    int lastRecite = (int) TimeUnit.SECONDS.toHours(response.getLastRecite());
    if (lastRecite >= 24) {
      long lastSeen = (int) TimeUnit.HOURS.toDays(lastRecite);
      view.changeTextCount((int) lastSeen, response.getTotalSubmission());
    } else {
      view.changeTextCount(lastRecite, response.getTotalSubmission());
    }

    //Check CsState
    boolean csState_online = response.isCredible();
    boolean csState_local = model.getCsState();
    if (csState_online != csState_local) {
      Timber.d("Saves cs state %s", csState_online);
      model.setCsState(csState_online);
    }

    //Set circle progress bar
    if (!csState_online){
      view.updateCircleProgressBar(response.getRemainingTime(), response.getTotalTime());
    } else {
      view.updateCircleProgressBar(1, 1);
    }

    //Set reciteTime
    String time = String.format(Locale.US, "%02d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(response.getRemainingTime()),
        TimeUnit.MILLISECONDS.toMinutes(response.getRemainingTime()) -
            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(response.getRemainingTime())),
        TimeUnit.MILLISECONDS.toSeconds(response.getRemainingTime()) -
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(response.getRemainingTime())));
    view.setReciteTime(time);

    //Check for valid email
    if (!response.isValidEmail()) {
      view.showUpdateEmailDialog();
    }

    //Check for ads
    if (response.isShowAdvert() && !response.isCredible()) {

      String adsMessage = response.getAdMessage();
      String adsImage = response.getAdImage();
      String adsUrl = response.getAdUrl();

      if (!TextUtils.isEmpty(adsMessage) && !TextUtils.isEmpty(adsImage) && !TextUtils.isEmpty(adsUrl)) {
        view.showAdsDialog(adsMessage,
            adsImage,
            adsUrl);
      }
    }

    //Check if quota reach
    model.setDashboardData(response);

    //Setup dashboard item
    //Enable CouponCall/Redemption item
    view.updateDrawerItem(2, true);
    //Enable Referral Item
    view.updateDrawerItem(1, true);

    if (!response.isCredible()) {
        //Enable item reload
        view.updateDrawerItem(3, true);
    } else {
      //Disable item reload
      view.updateDrawerItem(3, false);
    }

  }

  //unSubscribe dashboard
  @Override
  public void unSubscribe() {
    Timber.e("unSubscribe Presenter");
    subscriptions.unsubscribe();
    view = null;
  }
}
