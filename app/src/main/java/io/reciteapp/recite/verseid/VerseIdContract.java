package io.reciteapp.recite.verseid;

import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.model.QuranSearchResultResponse;

public interface VerseIdContract {

  interface View {
    void showWait();
    void removeWait();
    void showErrorView(int responseCode);
    void onSuccess(QuranSearchResultResponse result);
    void logout();
  }

  interface Model {
    String getToken();
  }

  interface Presenter {
    void setView(View view, NetworkCallWrapper service);

    void sendResult(String s);

    void unSubscribe();
    }

}
