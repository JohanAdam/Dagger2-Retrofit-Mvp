package io.reciteapp.recite.submissiondetail.cs.recordcomment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.model.UstazReviewCommentResponse;
import io.reciteapp.recite.submissiondetail.cs.recordcomment.DialogRecordContract.Presenter;
import io.reciteapp.recite.utils.RecitePlayerContract.playerErrorStateCallback;
import io.reciteapp.recite.utils.RecitePlayerContract.playerStateCallback;
import io.reciteapp.recite.utils.Rplayer;
import timber.log.Timber;

public class DialogRecordComment implements OnClickListener, DialogRecordContract.View {

  private Dialog dialog;
  private Context context;

  private TextView tvDuration;
  private EditText etComment;
  private ImageButton btnUserRecital, btnCorrection, btnDelete;
  private Button btn_Submit;

  private Presenter presenter;

  /**
   * @param userAudioUri User audio submission uri
   * @param audioFileName Audio file name for this comment record
   * @param ustazComment The data if the data available fo this duration
   */
  public DialogRecordComment(String userAudioUri,
      String audioFileName,
      UstazReviewCommentResponse ustazComment) {
    presenter = new DialogRecordPresenter();
    presenter.initDialog(this, userAudioUri, audioFileName, ustazComment);
  }

  public Dialog getDialog() {
    return dialog;
  }

  public void showDialog(Context context, DialogRecordContract.DialogRecordCommentCallback callback) {

    if (!((Activity) context).isFinishing()) {
      //setup and show dialog
      dialog = new Dialog(context, R.style.AppDialog);
      if (dialog.getWindow() != null) {
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      }
      dialog.setOnDismissListener(dialog -> {
        presenter.destroy();
      });
      dialog.setContentView(R.layout.dialog_record_comment);
      dialog.setCanceledOnTouchOutside(false);
      dialog.setCancelable(false);
      dialog.show();

      this.context = context;

      if (!dialog.isShowing()) {
        dialog.show();
      }

      tvDuration = dialog.findViewById(R.id.tv_duration);
      etComment = dialog.findViewById(R.id.et_comment);
      btnUserRecital = dialog.findViewById(R.id.btn_user_recital);
      btnCorrection = dialog.findViewById(R.id.btn_corection);
      btnDelete = dialog.findViewById(R.id.btn_delete);
      btn_Submit = dialog.findViewById(R.id.btn_submit);

      presenter.showDialog(callback);

      btnUserRecital.setOnClickListener(this);
      btnCorrection.setOnClickListener(this);
      btnDelete.setOnClickListener(this);
      btn_Submit.setOnClickListener(this);
    }
  }

  @Override
  public Rplayer getContextForPlayerUser(Rplayer playerUser,
      String userAudioUri,
      playerStateCallback playerUserStateListener,
      playerErrorStateCallback playerErrorListener) {
    playerUser.initPlayer(context, userAudioUri, playerUserStateListener, playerErrorListener);
    return playerUser;
  }

  @Override
  public Rplayer getContextForPlayerComment(Rplayer playerComment,
      String commentAudioUri,
      playerStateCallback playerCommentStateListener,
      playerErrorStateCallback playerErrorListener) {
    playerComment.initPlayer(context, commentAudioUri, playerCommentStateListener, playerErrorListener);
    return playerComment;
  }

  @Override
  public void setTextDurationToView(String duration) {
    tvDuration.setText(duration);
  }

  @Override
  public void setCommentToView(String comment) {
    etComment.setText(comment);
  }

  @Override
  public void viewAudioCsExist() {
    //Initialized playerComment
    presenter.initializedPlayerCs();

    btnCorrection.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
    btnDelete.setVisibility(View.VISIBLE);
  }

  @Override
  public void viewAudioCsNotExist() {
    //Initialized recorder
    presenter.initializedRecorder();

    btnCorrection.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_record_white));
    btnDelete.setVisibility(View.GONE);
  }

  //Dismiss the dialog
  @Override
  public void removeDialog() {
    try {
      if (dialog != null) {
        if (dialog.isShowing()) {
          dialog.dismiss();
        }
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void userPlayBtnEnable() {
    btnUserRecital.setEnabled(true);
    btnUserRecital.setBackground(context.getResources().getDrawable(R.drawable.button_background_pink));
  }

  @Override
  public void userPlayBtnDisable() {
    btnUserRecital.setEnabled(false);
    btnUserRecital.setBackground(context.getResources().getDrawable(R.drawable.button_background));
  }

  @Override
  public void submitBtnEnable() {
    btn_Submit.setEnabled(true);
    btn_Submit.setTextColor(ContextCompat.getColor(context, R.color.white));
  }

  @Override
  public void submitBtnDisable() {
    btn_Submit.setEnabled(false);
    btn_Submit.setTextColor(ContextCompat.getColor(context, R.color.disable_item_text));
  }

  @Override
  public void isRecordingView() {
    btnCorrection.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_stop_white));
    submitBtnDisable();
  }

  @Override
  public void btnUserError() {
    if (dialog.isShowing()) {
      btnUserRecital.clearAnimation();
      btnUserRecital.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_error_white_24dp));
    }
  }

  @Override
  public void btnUserBuffering() {
    if (dialog.isShowing()) {
      btnUserRecital.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_refresh));
      Animation animRotate = AnimationUtils.loadAnimation(context, R.anim.anim_rotate);
      animRotate.setRepeatCount(Animation.INFINITE);
      btnUserRecital.startAnimation(animRotate);
    }
  }

  @Override
  public void btnUserReady(boolean playWhenReady) {
    if (dialog.isShowing()) {
      btnUserRecital.clearAnimation();
      if (playWhenReady) {
        btnUserRecital.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause_white_24dp));
      } else {
        btnUserRecital.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
      }
    }
  }

  @Override
  public void btnUserEnded() {
    if (dialog.isShowing()) {
      btnUserRecital.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
    }
  }

  @Override
  public void btnCsError() {
    if (dialog.isShowing()) {
      btnCorrection.clearAnimation();
      btnCorrection.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_error_white_24dp));
    }
  }

  @Override
  public void btnCsBuffering() {
    if (dialog.isShowing()) {
      btnCorrection.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_refresh));
      Animation animRotate =AnimationUtils.loadAnimation(context, R.anim.anim_rotate);
      animRotate.setRepeatCount(Animation.INFINITE);
      btnCorrection.startAnimation(animRotate);
    }
  }

  @Override
  public void btnCsReady(boolean playWhenReady) {
    if (dialog.isShowing()) {
      btnCorrection.clearAnimation();
      if (playWhenReady) {
        btnCorrection.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause_white_24dp));
      } else {
        btnCorrection.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
      }
    }
  }

  @Override
  public void btnCsEnded() {
    if (dialog.isShowing()) {
      btnCorrection.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
    }
  }


  @Override
  public void onClick(View v) {
    Timber.d("onClick DialogRecordComment");

    switch (v.getId()) {
      case R.id.btn_submit:
        presenter.submit(etComment.getText().toString());
        break;
      case R.id.btn_user_recital:
        presenter.playerUserToggle();
        break;
      case R.id.btn_corection:
        presenter.playerCsToggle();
        break;
      case R.id.btn_delete:
        presenter.deleteAudioComment();
        break;
    }
  }


}

