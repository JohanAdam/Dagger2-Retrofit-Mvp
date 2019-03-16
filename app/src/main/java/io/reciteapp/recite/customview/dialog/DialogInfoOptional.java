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

/**
 * Dialog Info with Yes or No
 */

public class DialogInfoOptional implements OnClickListener {

  private TextView tvTitle;
  private TextView tvStatus;
  private Button btnOk;
  private Button btnCancel;

  private Dialog dialogInfoOptional;
  private DialogInfoOptionalCallback listener;

  public DialogInfoOptional() {
  }

  public Dialog getDialogInfoOptional() {
    return dialogInfoOptional;
  }

  public void showDialog(Context context, String title, String status, DialogInfoOptionalCallback listener) {

    if (!((Activity) context).isFinishing()) {
      //show dialog
      dialogInfoOptional = new Dialog(context, R.style.AppDialog);
      if (dialogInfoOptional.getWindow() != null) {
        dialogInfoOptional.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      }
      dialogInfoOptional.setContentView(R.layout.dialog_info_optional);
      dialogInfoOptional.show();

      this.listener = listener;


      tvTitle = dialogInfoOptional.findViewById(R.id.tv_title);
      tvStatus = dialogInfoOptional.findViewById(R.id.tv_empty);
      btnOk = dialogInfoOptional.findViewById(R.id.btn_ok);
      btnCancel = dialogInfoOptional.findViewById(R.id.btn_cancel);

      tvTitle.setText(title);
      tvStatus.setText(status);
      btnOk.setOnClickListener(this);
      btnCancel.setOnClickListener(this);

      if (!dialogInfoOptional.isShowing()) {
        dialogInfoOptional.show();
      }
    }
  }

  public void removeDialog() {
    try {
      if (dialogInfoOptional != null) {
        if (dialogInfoOptional.isShowing()) {
          dialogInfoOptional.dismiss();
        }
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_ok:
        if  (dialogInfoOptional.isShowing()) {
          listener.onClick(R.id.btn_ok);
          removeDialog();
        }
        break;
      case R.id.btn_cancel:
        if (dialogInfoOptional.isShowing()) {
          listener.onClick(R.id.btn_cancel);
          removeDialog();
        }
        break;
      default:
        removeDialog();
        break;
    }
  }

  public interface DialogInfoOptionalCallback {
    void onClick(int btnClick);
  }
}
