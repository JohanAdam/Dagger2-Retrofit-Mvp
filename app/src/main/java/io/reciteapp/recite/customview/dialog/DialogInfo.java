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
 * Dialog Info only with Yes
 */

public class DialogInfo implements OnClickListener {

  private TextView tvTitle;
  private TextView tvStatus;
  private Button btnOk;

  private Dialog dialogInfo;
  private DialogInfoCallback listener;

  public DialogInfo() {
  }

  public Dialog getDialogInfo() {
    return dialogInfo;
  }

  public void showDialog(Context context, String title, String status, DialogInfoCallback listener) {

    if (!((Activity) context).isFinishing()) {
      //show dialog
      dialogInfo = new Dialog(context, R.style.AppDialog);
      if (dialogInfo.getWindow() != null) {
        dialogInfo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      }
      dialogInfo.setContentView(R.layout.dialog_info);
      dialogInfo.show();

      this.listener = listener;


      tvTitle = dialogInfo.findViewById(R.id.tv_title);
      tvStatus = dialogInfo.findViewById(R.id.tv_empty);
      btnOk = dialogInfo.findViewById(R.id.btn_ok);

      tvTitle.setText(title);
      tvStatus.setText(status);
      btnOk.setOnClickListener(this);

      if (!dialogInfo.isShowing()) {
        dialogInfo.show();
      }
    }
  }

  public void removeDialog() {
    try {
      if (dialogInfo != null) {
        if (dialogInfo.isShowing()) {
          dialogInfo.dismiss();
        }
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void onClick(View v) {
    if  (dialogInfo.isShowing()) {
      if (listener != null) {
        listener.onClick();
      }
      removeDialog();
    }
  }

  public interface DialogInfoCallback {
    void onClick();
  }
}
