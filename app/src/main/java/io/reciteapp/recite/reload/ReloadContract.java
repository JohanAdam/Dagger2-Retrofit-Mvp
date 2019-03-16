package io.reciteapp.recite.reload;

import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.model.PackageListResponse;
import io.reciteapp.recite.data.model.PackageResponse;

public interface ReloadContract {

  interface View {

    void showWait();

    void removeWait();

    void showLoadingDialog();

    void removeLoadingDialog();

    void setReloadPackageToList(PackageResponse responses);

    void payment(PackageListResponse data);

    void showDialogReload(PackageListResponse date);

    void showWebReload(PackageListResponse data);

    void removeDialogReload();

    void showInfoDialog(String title, String desc);

    void showSuccessReloadDialog(String numberPhone);

    void showErrorDialog(int responseCode, boolean isKick);

    void showErrorLayout();

    void removeErrorLayout();

    void showNoPackageLayout();

    void removeNoPackageLayout();

    void logout();
  }

  interface Model {

    String getToken();

    String getCountry();
  }

  interface Presenter {

    void setView(View view, NetworkCallWrapper Service);

    void getPackageList();

    void payment(PackageListResponse data);

    void getPayment(String phone_number, String provider, String packageId);

    void unSubscribe();
  }

}
