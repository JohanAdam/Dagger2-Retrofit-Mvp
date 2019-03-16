package io.reciteapp.recite.progress;

import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.model.HistoryResponse;
import java.util.ArrayList;

public interface ProgressContract {

  interface View {

    void showWait();

    void removeWait();

    void setHistoryListToRv(ArrayList<HistoryResponse> responses);

    void showErrorLayout();

    void removeErrorLayout();

    void showNoResultLayout();

    void removeNoResultLayout();

    void logout();
  }

  interface Model {

    String getToken();

    boolean getLoginStatus();

  }

  interface Presenter {

    void setView(View view, NetworkCallWrapper service);

    boolean getLoginStatus();

    void getHistoryList();

    void unSubscribe();
  }

}
