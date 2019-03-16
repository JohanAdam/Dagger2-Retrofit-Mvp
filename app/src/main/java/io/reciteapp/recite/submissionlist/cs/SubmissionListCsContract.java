package io.reciteapp.recite.submissionlist.cs;

import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.model.SubmissionListResponse;
import java.util.List;

public interface SubmissionListCsContract {

  interface View {

    void showWait();

    void removeWait();

    void setSortText(int radioId);

    void setSubmissionListoRv(List<SubmissionListResponse> responses);

    void removeSubmissionList();

    void showErrorLayout();

    void removeErrorLayout();

    void showEmptyLayout();

    void removeEmptyLayout();

    void logout();

  }

  interface Model {

    String getToken();

    void saveSortPref(int orderListSort);

    int loadSortPref();

  }

  interface Presenter {

    void setView(View view, NetworkCallWrapper service);

    void setSortPreferences(int orderListSort);

    void getSortPreferences();

    void unSubscribe();

  }

}
