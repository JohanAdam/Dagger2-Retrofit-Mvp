package io.reciteapp.recite.surahlist;

import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.model.SurahListResponse;
import java.util.ArrayList;

public interface SurahListContract {

  interface View {

    void showWait();

    void removeWait();

    void setSurahList(ArrayList<SurahListResponse> responses);

    void setSurahListMain(ArrayList<SurahListResponse> response);

    ArrayList<SurahListResponse> getSurahListMain();

    void removeSurahList();

    void showErrorLayout();

    void removeErrorLayout();

    void showNoResultLayout();

    void removeNoResultLayout();

    void logout();

  }

  interface Model {

    String getToken();

  }

  interface Presenter {

    void setView(View view, NetworkCallWrapper service);

    void getSurahList();

    void filteredList(String searchText);

    void unSubscribe();
  }

}
