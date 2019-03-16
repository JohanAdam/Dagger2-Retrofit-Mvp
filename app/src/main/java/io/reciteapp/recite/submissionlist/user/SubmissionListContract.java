package io.reciteapp.recite.submissionlist.user;

import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.model.SubmissionListResponse;
import java.util.List;

public interface SubmissionListContract {

  interface View {

    void showWait();

    void removeWait();

    void setSubmissionListToRv(List<SubmissionListResponse> responses);

    void removeSubmissionList();

    void showErrorLayout();

    void removeErrorLayout();

    void showNoResultLayout();

    void removeNoResultLayout();

    void logout();
  }

  interface Model {

    String getToken();

    void setSubmissionList(List<SubmissionListResponse> submissionList);

    List<SubmissionListResponse> getSubmissionList();
  }

  interface Presenter {

    void setView(View view, NetworkCallWrapper service);

    void getSubmissionList();

    void filteredList(String query);

    void unSubscribe();
  }

}
