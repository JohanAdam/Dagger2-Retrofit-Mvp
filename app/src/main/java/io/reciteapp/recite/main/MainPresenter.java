package io.reciteapp.recite.main;

import android.text.TextUtils;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.call.CouponCall.PostCouponCallback;
import io.reciteapp.recite.data.networking.call.ReferralCall.PatchReferralCodeCallback;
import io.reciteapp.recite.data.networking.call.UserProfileCall.GetUserProfileResponseCallback;
import io.reciteapp.recite.main.MainContract.Model;
import io.reciteapp.recite.main.MainContract.Presenter;
import io.reciteapp.recite.main.MainContract.View;
import io.reciteapp.recite.data.model.CouponResponse;
import io.reciteapp.recite.data.model.UserProfileResponse;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainPresenter implements Presenter {

  private View view;
  private MainContract.Model model;
  private NetworkCallWrapper service;
  private CompositeSubscription subscriptions;

  public MainPresenter(Model model) {
    this.subscriptions = new CompositeSubscription();
    this.model = model;
  }

  @Override
  public void setView(View view, NetworkCallWrapper service) {
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

  //Get resources drawable item by sending position
  @Override
  public int getDrawerItemIcon(int position) {
    switch (position) {
      case 1:
        return R.drawable.ic_reffral;
      case 2:
        return R.drawable.ic_redemption;
      case 3:
        return R.drawable.ic_reload;
      case 4:
        return R.drawable.ic_reviewer_test;
      case 5:
        return R.drawable.ic_settings;
      case 6:
        return R.drawable.ic_quick_start;
      case 7:
        return R.drawable.ic_share;
      case 8:
        return R.drawable.ic_about;
    }
    return R.drawable.ic_verse_id;
  }

  //Get title of drawer item by sending position
  @Override
  public int getDrawerItemTitle(int position) {
    switch (position) {
      case 1:
        return R.string.title_referral;
      case 2:
        return R.string.title_redemption;
      case 3:
        return R.string.title_reload;
      case 4:
        return R.string.title_reviewer_test;
      case 5:
        return R.string.title_settings;
      case 6:
        return R.string.title_quick_start;
      case 7:
        return R.string.title_share;
      case 8:
        return R.string.title_about;
    }
    return R.string.app_name;
  }

  // > processReferral > checkUserProfile > patchReferralCodeToUserProfile
//                  > showEnrollDialog > checkUserProfile > patchReferralCodeToUserProfile
  @Override
  public void processReferral(String refCode) {
    String json = model.getEnrollList();
    try {
      List<String> enrollList = jsonStringToArray(json);
      Timber.d("enrollList is %s", enrollList);
      if (enrollList != null && !enrollList.isEmpty()) {
        if (enrollList.contains(refCode)) {
          if (isViewAttached()) {
            view.showEnrollDialog(refCode);
          }
        } else {
          patchReferralCodeToUserProfile(refCode);
        }
      } else {
        patchReferralCodeToUserProfile(refCode);
      }
    } catch (JSONException e) {
      e.printStackTrace();
      patchReferralCodeToUserProfile(refCode);
    }
  }

  @Override
  public void patchReferralCodeToUserProfile(String refCode) {
    view.showLoadingDialog();

    //check for referral code is available in server or not
    checkReferralCodeInUserProfile(new CheckReferralCodeCallback() {
      @Override
      public void onResult(boolean referralCodeAvailable) {
        if (referralCodeAvailable) {
          //if available , just update the view
          if (isViewAttached()){
            view.removeLoadingDialog();
            view.showErrorDialog(Constants.RESPONSE_CODE_ACCOUNT_ALREADY_HAS_REFERRAL_CODE, false);
          }
        } else {
          //if not available , patch the referral code to server
          String token = model.getToken();
          Subscription subscription = service.patchReferralCode(token, refCode, new PatchReferralCodeCallback() {
            @Override
            public void onSuccess(String result) {
              if (isViewAttached()){
                model.setReferralCode(refCode);
                view.removeLoadingDialog();
              }
            }

            @Override
            public void onError(int responseCode) {
              if (isViewAttached()){
                view.removeLoadingDialog();
                view.showErrorDialog(responseCode, false);
              }
            }
          });

          subscriptions.add(subscription);
        }
      }
    });
  }

  @Override
  public void processCoupon(String coupon) {
    view.showLoadingDialog();

    String token = model.getToken();
    Subscription subscription = service.postCouponCode(token, coupon, new PostCouponCallback() {
      @Override
      public void onSuccess(CouponResponse response) {
        if (isViewAttached()) {
          view.removeLoadingDialog();
          if (response.getResponse() == Constants.RESPONSE_CODE_SUCCESS) {
            //Code redeem success

            //refresh
            view.openFirstViewFragment(Constants.TAG_MAIN_FRAGMENT, null,null);

            //show Sponsor dialog
            view.showSponsorDialog(response.getCouponMessage(),
                response.getCouponImage(),
                response.getCouponUrl(),
                true);
          } else if (response.getResponse() == Constants.RESPONSE_CODE_CODE_USED) {
            //Code has been used
            view.showErrorDialog(Constants.RESPONSE_CODE_CODE_USED, false);
          } else if (response.getResponse() == Constants.RESPONSE_CODE_CODE_EXPIRED) {
            //Code has expired
            view.showErrorDialog(Constants.RESPONSE_CODE_CODE_EXPIRED, false);
          } else {
            view.showErrorDialog(Constants.RESPONSE_CODE_FAILED, false);
          }
        }
      }

      @Override
      public void onError(int responseCode) {
        if (isViewAttached()) {
          view.removeLoadingDialog();
          if (responseCode == Constants.RESPONSE_CODE_NO_INTERNET) {
            view.showSnackBar(R.string.error_no_connection);
          } else {
            view.showErrorDialog(responseCode, false);
          }
        }
      }
    });
    subscriptions.add(subscription);
  }

  private List<String> jsonStringToArray(String jsonString) throws JSONException {
    List<String> stringArray = new ArrayList<String>();
    JSONArray jsonArray = new JSONArray(jsonString);
    for (int i = 0; i < jsonArray.length(); i++) {
      stringArray.add(jsonArray.getString(i));
    }
    return stringArray;
  }

  @Override
  public void checkBundle(boolean isError, String errorText,
      String fragment,
      String surahName,
      String ayatId) {

    if (isError && !TextUtils.isEmpty(errorText)) {
      //true , popup error
      //Authentication error
      view.showErrorDialog(Constants.RESPONSE_CODE_UNAUTHORIZED, false);
    }

    //If fragment is null, open dashboard
    //If fragment is contain HistoryTag, open history
    if (!TextUtils.isEmpty(fragment)) {
      if (fragment.equals(Constants.OPEN_FRAGMENT_HISTORY)) {
        //Open fragment History List (
        view.openFirstViewFragment(fragment, surahName, ayatId);
      } else {
        //Open fragment Submission List (New Submission)
        view.openFirstViewFragment(fragment, null, null);
      }
    } else {
      view.openFirstViewFragment(Constants.TAG_MAIN_FRAGMENT, null, null);
    }

  }

  @Override
  public boolean getCsStatus() {
    return model.getCsStatus();
  }

  @Override
  public void checkFirstUser() {
    if (model.getFirstUser()) {
      openQuickActivity();
    }
  }

  /**
   * Check onResume activity
   * if not login, send fragment to dashboard / refresh dashboard component
   */
  @Override
  public void checkLogin() {
    if (!model.getLoginStatus()) {
      view.openFirstViewFragment(Constants.TAG_MAIN_FRAGMENT, null, null);
    }
  }

  @Override
  public void openQuickActivity() {
    if (model.getCsStatus()) {
      view.openQuickStartCsActivity();
    } else {
      view.openQuickStartActivity();
    }
  }

  private interface CheckReferralCodeCallback {
    void onResult(boolean referralCodeAvailable);
  }

  //Check User Profile before submitting referral code
  private void checkReferralCodeInUserProfile(CheckReferralCodeCallback callback) {
    String token = model.getToken();
    Subscription subscription = service.getUserProfile(token,
        new GetUserProfileResponseCallback() {
          @Override
          public void onSuccess(ArrayList<UserProfileResponse> result) {
            //From result check is userprofile already have referral code or not
            if (isViewAttached()) {
              for (UserProfileResponse userProfileResponse : result) {
                if (!TextUtils.isEmpty(userProfileResponse.getReferralCode())) {
                  //if referral code is available
                  callback.onResult(true);
                } else {
                  //if referral code empty
                  callback.onResult(false);
                }
              }
            }
          }

          @Override
          public void onError(int responseCode) {
            if (isViewAttached()) {
              callback.onResult(false);
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

  /**
   * Logout button function
   */
  @Override
  public void logoutFunction() {
    if (model.getLoginStatus()) {
      //login
      view.logout();
    } else {
      //logout
      view.openLoginActivity();
    }
  }

  /**
   * Return title resource for log item based on user state
   * @return Title resource
   */
  @Override
  public int getLogItemTitle() {
    if (model.getLoginStatus()) {
      return R.string.title_logout;
    } else {
      return R.string.title_login;
    }
  }

  @Override
  public void logout() {
//    AzureServiceAdapter.getInstance().logout();
    model.logout();
    if (isViewAttached()) {
      view.updateLogItem();
    }
  }
}
