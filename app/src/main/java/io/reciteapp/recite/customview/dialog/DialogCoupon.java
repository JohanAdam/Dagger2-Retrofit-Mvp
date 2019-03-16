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

public class DialogCoupon implements OnClickListener {

  private EditText et_code;
  private Button btnSubmit, btnCancel;
  private Context context;

  private Dialog dialog;
  private DialogCouponCallback callback;

  public DialogCoupon() {
  }

  public Dialog getDialog() {
    return dialog;
  }

  public void showDialog(Context context, DialogCouponCallback callback) {

    if (!((Activity) context).isFinishing()) {
      //show dialog
      dialog = new Dialog(context, R.style.AppDialog);
      if (dialog.getWindow() != null) {
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      }
      dialog.setContentView(R.layout.dialog_coupon);
      dialog.show();

      this.callback = callback;
      this.context = context;

      et_code = dialog.findViewById(R.id.et_code);
      btnSubmit = dialog.findViewById(R.id.btn_submit);
      btnCancel = dialog.findViewById(R.id.btn_cancel);

      btnSubmit.setOnClickListener(this);
      btnCancel.setOnClickListener(this);

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
    Timber.d("onClick DialogCoupon");

    switch (v.getId()) {
      case R.id.btn_submit:
        if (!TextUtils.isEmpty(et_code.getText().toString())) {
          callback.onClick(et_code.getText().toString().toUpperCase());
          removeDialog();
        } else {
          et_code.setError(context.getString(R.string.error_please_enter_code));
        }
        break;
      case R.id.btn_cancel:
        callback.onClick("");
        removeDialog();
        break;
      default:
        removeDialog();
        break;
    }

  }

  public interface DialogCouponCallback {
    void onClick(String couponCode);
  }
}
