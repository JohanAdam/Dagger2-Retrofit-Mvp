package io.reciteapp.recite.dashboard;

import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.model.DashboardResponse;
import io.reciteapp.recite.data.model.RegisteredUserProfileResponse;

/**
 * Contract for Dashboard Page
 * 1
 */

public interface DashboardContract {

  interface View {

    void showWait();

    void removeWait();

    void showLoadingDialog();

    void removeLoadingDialog();

    void showInfoDialog(int titleId, int messageId);

    void showInfoDialog(String title, String message);

    void showShareDialog(int value);

    void showErrorDialog(int responseCode, boolean isKick);

    void showUpdateEmailDialog();

    void showAdsDialog(String message, String imageUrl, String redirectUrl);

    void updateDrawerItem(int position, boolean isEnable);

    //change Last Seen and Total Sub text
    void changeTextCount(int lastSeen, int totalSub);

    void setReciteTime(String time);

    void updateCircleProgressBar(int remainTime, int totalTime);
    
    void notLoginView();

    void showSnackBar(int stringId);

    void openSurahList();

    void openSubmissionList();

    void openSubmissionListCs();

    void openLoginActivity();

    void logout();
  }

  interface Model {

    String getToken();

    boolean getLoginStatus();

    void setDashboardData(DashboardResponse dashboardResponse);

    DashboardResponse getDashboardData();

    void setCsState(boolean csState);

    boolean getCsState();

    void setEmail(String email);

    String getCountry();

    String getMessage();

    int getReferralTime();

    void clearData(String key);

  }

  interface Presenter {

    void setView(View view, NetworkCallWrapper service);

    String getToken();

    boolean getLoginStatus();

    void checkNotification();

    void getDashboard();

    void openSurahList();

    void openSubmissionList();

    void postRegister(RegisteredUserProfileResponse dataModel, boolean checkRequiredPayment,
        String billPlzPackageID);

    void patchEmail(String email);

    void patchFreeCreditHistory(boolean isAccept, String freeCreditHistoryId);

    //unsubscribe subscriber
    void unSubscribe();

  }


}
