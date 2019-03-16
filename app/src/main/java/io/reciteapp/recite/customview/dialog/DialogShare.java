package io.reciteapp.recite.customview.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import io.reciteapp.recite.R;

public class DialogShare implements OnClickListener{
  public Dialog dialog;
  public DialogShare.DialogShareCallback callback;
  public ConstraintLayout btn_share;
  public TextView tvTitle, tvDesc;

  public DialogShare() {
  }

  public Dialog getDialogShare() {
    return dialog;
  }

  public void showDialog(Context context, String title, String desc, DialogShare.DialogShareCallback listener) {

    if (!((Activity) context).isFinishing()) {
      //show dialog
      dialog = new Dialog(context, R.style.AppDialog);
      if (dialog.getWindow() != null) {
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      }
      dialog.setContentView(R.layout.dialog_share);
      dialog.show();

      this.callback = listener;

      tvTitle = dialog.findViewById(R.id.tv_title);
      tvDesc = dialog.findViewById(R.id.tv_desc);
      btn_share = dialog.findViewById(R.id.btn_share);

      tvTitle.setText(title);
      tvDesc.setText(desc);
      btn_share.setOnClickListener(this);

      if (!dialog.isShowing()) {
        dialog.show();
      }
    }
  }

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
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_share:
        if  (dialog.isShowing()) {
          callback.onClick();
          removeDialog();
        }
        break;
      default:
        removeDialog();
        break;
    }
  }

  public interface DialogShareCallback {
    void onClick();
  }
}
