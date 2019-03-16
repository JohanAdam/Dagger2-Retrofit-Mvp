package io.reciteapp.recite.share;

import io.reciteapp.recite.data.networking.NetworkCallWrapper;

public interface ShareContract  {

  interface View {

    void showWait();

    void removeWait();

    void notLoginLayout();

    void onSuccessLayout(String shareCode);

    void showErrorLayout();

    void removeErrorLayout();

    void showToast(int textR);

    void shareCode(String shareCode);

    void logout();

  }

  interface Model {

    String getToken();

    void setShareCode(String referralCode);

    String getShareCode();

    String getUserId();

    void copyShareCode();

    boolean checkLogin();
  }

  interface Presenter {

    void setView(View view, NetworkCallWrapper service);

    void checkLogin();

    void getReferralCode();

    void copyCode();

    void shareCode();

    void postActionStats(String action);

    void unSubscribe();

  }

}
