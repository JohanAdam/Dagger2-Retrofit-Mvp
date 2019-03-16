package io.reciteapp.recite.main;

import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import java.util.ArrayList;

public interface MainContract{

  interface View {

    ArrayList<PrimaryDrawerItem> setupDrawerItem();

    void updateDrawerItem(int position, boolean isEnable);

    void updateLogItem();

    void showSnackBar(int messageResources);

    void showLoadingDialog();

    void removeLoadingDialog();

    void showErrorDialog(int responseCode, boolean isKick);

    void showInfoDialog(int titleId, int messageId);

    void showInfoDialog(String title, String message);

    void showShareDialog(String title, String desc);

    void showUpdateDialog();

    void showReferralDialog();

    void showEnrollDialog(String referralCode);

    void showCouponDialog();

    void showSponsorDialog(String message,
        String imageUrl,
        String redirectUrl,
        boolean disableCancel);

    void openReloadActivity();

    void openReviewerTestActivity();

    void openSettingsActivity();

    void openQuickStartActivity();

    void openQuickStartCsActivity();

    void openAboutActivity();

    void openLoginActivity();

    void openFirstViewFragment(String tag, String surahName, String ayatId);

    void logout();
  }

  interface Model {

    void logout();

    boolean getLoginStatus();

    void setReferralCode(String referralCode);

    String getEnrollList();

    String getToken();

    boolean getCsStatus();

    boolean getFirstUser();
  }

  interface Presenter {

    void setView(View view, NetworkCallWrapper service);

    int getDrawerItemIcon(int position);

    int getDrawerItemTitle(int position);

    boolean getCsStatus();

    void checkFirstUser();

    void checkLogin();

    void openQuickActivity();

    void processReferral(String refCode);

    void patchReferralCodeToUserProfile(String refCode);

    void processCoupon(String coupon);

    void checkBundle(boolean isError, String errorText, String fragment, String surahName,
        String ayatId);

    void unSubscribe();

    void logoutFunction();

    int getLogItemTitle();

    void logout();
    }

}
