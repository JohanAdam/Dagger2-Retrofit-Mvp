package io.reciteapp.recite.customview.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.widget.TextView;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.ProgressBarAnimation;
import io.reciteapp.recite.root.App;

public class DialogLoading {

  private Dialog dialogLoading;
  private ProgressBarAnimation progressBarAnimation;

  public DialogLoading() {
    progressBarAnimation = new ProgressBarAnimation();
  }

  public Dialog getDialog(){
    return dialogLoading;
  }

  public void showLoadingDialog(Context context, String text_status) {

    if (!((Activity) context).isFinishing()) {
      //show dialog
      dialogLoading = new Dialog(context, R.style.AppDialog);
      if (dialogLoading.getWindow() != null) {
        dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      }
      dialogLoading.setOnDismissListener(dialog -> progressBarAnimation.stopAnimation());
      dialogLoading.setContentView(R.layout.dialog_loading);
      dialogLoading.setCanceledOnTouchOutside(false);
      dialogLoading.setCancelable(false);
      dialogLoading.show();

      AppCompatImageView imageView = dialogLoading.findViewById(R.id.progressBar);
      final AnimatedVectorDrawableCompat animatedVector = AnimatedVectorDrawableCompat.create(App.getApp(), R.drawable.anim_loading);
      imageView.setImageDrawable(animatedVector);

      progressBarAnimation.startAnimation(imageView);

      TextView tvStatus = dialogLoading.findViewById(R.id.tv_empty);
      tvStatus.setText(text_status);

      if (!dialogLoading.isShowing()) {
        dialogLoading.show();
      }
    }
  }

  public void removeLoadingDialog() {
    try {
      if (dialogLoading != null) {
        if (dialogLoading.isShowing()) {
          dialogLoading.dismiss();
        }
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

}
