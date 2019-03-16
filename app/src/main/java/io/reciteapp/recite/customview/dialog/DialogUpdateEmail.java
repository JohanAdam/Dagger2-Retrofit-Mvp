package io.reciteapp.recite.customview.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import io.reciteapp.recite.R;
import io.reciteapp.recite.utils.Utils;
import timber.log.Timber;

public class DialogUpdateEmail implements OnClickListener {

  private EditText etEmail;
  private Button btnOk;
  private Context context;

  private Dialog DialogUpdateEmail;
  private DialogUpdateEmailCallback callback;

  public DialogUpdateEmail() {
  }

  public Dialog getDialogUpdateEmail() {
    return DialogUpdateEmail;
  }

  public void showDialog(Context context, DialogUpdateEmailCallback callback) {

    if (!((Activity) context).isFinishing()) {
      //show dialog
      DialogUpdateEmail = new Dialog(context, R.style.AppDialog);
      if (DialogUpdateEmail.getWindow() != null) {
        DialogUpdateEmail.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      }
      DialogUpdateEmail.setContentView(R.layout.dialog_update_email);
      DialogUpdateEmail.setCanceledOnTouchOutside(false);
      DialogUpdateEmail.setCancelable(false);
      DialogUpdateEmail.show();

      this.callback = callback;
      this.context = context;

      etEmail = DialogUpdateEmail.findViewById(R.id.et_email);
      btnOk = DialogUpdateEmail.findViewById(R.id.btn_submit);

      btnOk.setOnClickListener(this);

      if (!DialogUpdateEmail.isShowing()) {
        DialogUpdateEmail.show();
      }
    }
  }

  public void removeDialog() {
    try {
      if (DialogUpdateEmail != null) {
        if (DialogUpdateEmail.isShowing()) {
          DialogUpdateEmail.dismiss();
        }
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void onClick(View v) {
    Timber.d("onClick DialogUpdatedEmail");
    if  (DialogUpdateEmail.isShowing()) {
      if (!Utils.isValidEmail(etEmail.getText().toString())) {
        if (context != null) {
          Timber.d("Toast");
          Utils utils = new Utils();
          utils.getToast(context, context.getString(R.string.msg_please_insert_valid_email)).show();
        }
      } else {
        Timber.d("Submit");
        callback.onClick(etEmail.getText().toString());
        removeDialog();
      }
    }
  }

  public interface DialogUpdateEmailCallback {
    void onClick(String email);
  }
}
