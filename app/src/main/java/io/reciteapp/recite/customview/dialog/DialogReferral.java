package io.reciteapp.recite.customview.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import io.reciteapp.recite.R;
import timber.log.Timber;

public class DialogReferral implements OnClickListener {

  private EditText et_ref_code;
  private Button btnSubmit, btnCancel;
  private Context context;

  private Dialog dialog;
  private DialogReferralCallback callback;

  public DialogReferral() {
  }

  public Dialog getDialog() {
    return dialog;
  }

  public void showDialog(Context context, DialogReferralCallback callback) {

    if (!((Activity) context).isFinishing()) {
      //show dialog
      dialog = new Dialog(context, R.style.AppDialog);
      if (dialog.getWindow() != null) {
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      }
      dialog.setContentView(R.layout.dialog_referral);
      dialog.show();

      this.callback = callback;
      this.context = context;

      et_ref_code = dialog.findViewById(R.id.et_code);
      btnSubmit = dialog.findViewById(R.id.btn_submit);
      btnCancel = dialog.findViewById(R.id.btn_cancel);

      btnSubmit.setOnClickListener(this);
      btnCancel.setOnClickListener(this);
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
    Timber.d("onClick DialogReferral");

    switch (v.getId()) {
      case R.id.btn_submit:
        if (!TextUtils.isEmpty(et_ref_code.getText().toString())) {
          callback.onClick(et_ref_code.getText().toString().toUpperCase());
        } else {
          et_ref_code.setError(context.getString(R.string.error_please_enter_code));
        }
        break;
      case R.id.btn_cancel:
        callback.onClick("");
        removeDialog();
        break;
    }

  }

  public interface DialogReferralCallback {
    void onClick(String refCode);
  }
}
