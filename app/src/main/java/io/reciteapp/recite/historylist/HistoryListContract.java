package io.reciteapp.recite.historylist;

import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.model.HistoryResponse;
import java.util.ArrayList;

public interface HistoryListContract {

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

    void setSurahName(String surahName);

    String getSurahName();

    void setSurahId(String surahId);

    String getSurahId();

  }

  interface Presenter {

    void setView(View view, NetworkCallWrapper service);

    void setSurahName(String surahName);

    void setSurahId(String surahId);

    void getHistoryList();

    void unSubscribe();
  }

}
