package io.reciteapp.recite.csprofile;

import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.model.CsProfileResponse;

public interface CsProfileContract {

  interface View {

    void showWait();

    void removeWait();

    void setData(CsProfileResponse response);

    void showErrorLayout();

    void logout();
  }

  interface Model {

    String getToken();

    void setId(String id);

    String getId();
    }

  interface Presenter {

    void setView(View view, NetworkCallWrapper service);

    void setId(String id);

    void getCsProfile();

    void unSubscribe();

  }

}
