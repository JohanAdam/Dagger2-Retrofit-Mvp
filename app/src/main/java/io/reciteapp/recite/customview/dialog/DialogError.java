package io.reciteapp.recite.customview.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.CustomTextView;

public class DialogError implements OnClickListener {

  CustomTextView tvIcon;
  TextView tvStatus;
  Button btnOk;

  private Dialog dialogError;
  private DialogErrorCallback callback;

  public DialogError() {
  }

  public Dialog getDialogError() {
    return dialogError;
  }

  public void showDialog(Context context,String status, DialogErrorCallback errorCallback) {

    if (!((Activity) context).isFinishing()) {
      //show dialog
      dialogError = new Dialog(context, R.style.AppDialog);
      if (dialogError.getWindow() != null) {
        dialogError.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      }
      dialogError.setContentView(R.layout.dialog_error);
      dialogError.show();

      if (errorCallback != null) {
        this.callback = errorCallback;
      }

      tvIcon = dialogError.findViewById(R.id.tv_title);
      tvStatus = dialogError.findViewById(R.id.tv_empty);
      btnOk = dialogError.findViewById(R.id.btn_ok);

      tvIcon.setText(R.string.icon_error);
      tvStatus.setText(status);
      btnOk.setOnClickListener(this);

      if (!dialogError.isShowing()) {
        dialogError.show();
      }
    }
  }

  public void removeDialog() {
    try {
      if (dialogError != null) {
        if (dialogError.isShowing()) {
          dialogError.dismiss();
        }
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void onClick(View v) {
    removeDialog();
    if (callback != null) {
      callback.onClick();
    }
  }

  public interface DialogErrorCallback {
    void onClick();
  }
}
