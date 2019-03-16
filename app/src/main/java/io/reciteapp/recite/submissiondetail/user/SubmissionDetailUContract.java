package io.reciteapp.recite.submissiondetail.user;

import io.reciteapp.recite.data.model.HistoryDetailResponse;
import io.reciteapp.recite.data.model.ReviewerCommentResponse;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.utils.Rplayer;
import java.util.List;

public interface SubmissionDetailUContract {

  interface View {

    String getStringFromResource(int messageResource);

    void showWait();

    void removeWait();

    void showLoadingDialog();

    void removeLoadingDialog();

    void showSnackBar(int messageResource);

    void showErrorDialog(int responseCode, boolean isKick);

    void showRatingDialog();

    void showErrorView();

    void removeErrorView();

    void setCsProfile(HistoryDetailResponse historyDetailResponse);

    void openCsProfileFragment(String id);

    void showSponsorCsDialog(String message,
        String imageUrl,
        String redirectUrl,
        boolean disableCancel);

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
  }

  interface Model {

    String getToken();

    void setId(String id);

    String getId();

    void setUserAudioUri(String userAudioUri);

    String getUserAudioUri();
  }

  interface Presenter {

    void setView(View view, NetworkCallWrapper service);

    void setId(String id);

    void getSubmissionDetail();

    void postAudioSubmissionRating(int rating);

    void openCsProfileFragment();

    void initializedPlayer(Rplayer rplayer);

    void playPauseToggle();

    void seekTo(long value);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void unSubscribe();

  }

}
