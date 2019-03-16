package io.reciteapp.recite.historydetail;

import io.reciteapp.recite.data.model.HistoryDetailResponse;
import io.reciteapp.recite.data.model.ReviewerCommentResponse;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.utils.Rplayer;
import java.util.List;

public interface HistoryDetailContract {

  interface View {

    String getStringFromResource(int resource);

    void showWait();

    void removeWait();

    void showLoadingDialog();

    void removeLoadingDialog();

    void showSnackbar(int messageResource);

    void showErrorDialog(int responseCode, boolean isKick);

    void showRatingDialog();

    void showErrorView();

    void removeErrorView();

    void setCsProfile(HistoryDetailResponse historyDetailResponse);

    void setCommentListToRv(List<ReviewerCommentResponse> commentList, String userAudioUri);

    void showNoComment();

    void removeNoComment();

    void showBeforeRatedCs();

    void showAfterRatedCs();

    void showNotReviewView();

    void hidePlayerView();

    void initializedPlayer(String audioUri);

    void playingAudioView();

    void pauseAudioView();

    void openCsProfileFragment(String id);
  }


  interface Model {

    String getToken();

    void setId(String id);

    String getId();

    void setAudioUri(String audioUri);

    String getAudioUri();
  }


  interface Presenter {

    void setView(View view, NetworkCallWrapper service);

    void setId(String id);

    void getHistoryDetail();

    void patchRatingCs(int rating, String feedback);

    void openCsProfileFragment();

    void initializedPlayer(Rplayer rplayer);

    void playPauseToggle();

    void seekTo(long value);

    void onPauseAudioOperation();

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void unSubscribe();

  }

}
