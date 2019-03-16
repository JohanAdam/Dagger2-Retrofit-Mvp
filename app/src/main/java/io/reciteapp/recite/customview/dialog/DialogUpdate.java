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

public class DialogUpdate implements OnClickListener {

  private TextView tvTitle;
  private TextView tvStatus;
  private Button btnOk;

  private Dialog dialogUpdate;
  private DialogUpdateCallback listener;

  public DialogUpdate() {
  }

  public Dialog getDialogUpdate() {
    return dialogUpdate;
  }

  public void showDialog(Context context, String title, String status, DialogUpdateCallback listener) {

    if (!((Activity) context).isFinishing()) {
      //show dialog
      dialogUpdate = new Dialog(context, R.style.AppDialog);
      if (dialogUpdate.getWindow() != null) {
        dialogUpdate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      }
      dialogUpdate.setContentView(R.layout.dialog_info);
      dialogUpdate.setCanceledOnTouchOutside(false);
      dialogUpdate.setCancelable(false);
      dialogUpdate.show();

      this.listener = listener;


      tvTitle = dialogUpdate.findViewById(R.id.tv_title);
      tvStatus = dialogUpdate.findViewById(R.id.tv_empty);
      btnOk = dialogUpdate.findViewById(R.id.btn_ok);

      tvTitle.setText(title);
      tvStatus.setText(status);
      btnOk.setOnClickListener(this);

      if (!dialogUpdate.isShowing()) {
        dialogUpdate.show();
      }
    }
  }

  public void removeDialog() {
    try {
      if (dialogUpdate != null) {
        if (dialogUpdate.isShowing()) {
          dialogUpdate.dismiss();
        }
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void onClick(View v) {
    if  (dialogUpdate.isShowing()) {
      if (listener != null) {
        listener.onClick();
      }
    }
  }

  public interface DialogUpdateCallback {
    void onClick();
  }
}
