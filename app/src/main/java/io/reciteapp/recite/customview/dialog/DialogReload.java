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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.model.PackageListResponse;

/**
 * Dialog Info only with Yes
 */

public class DialogReload implements OnClickListener {

  private EditText et_NumberPhone;
  private RadioGroup radioGroup;
  private Button btnSubmit;

  private Dialog dialog;
  private dialogReloadCallback listener;

  private Context context;
  private String provider = "";
  PackageListResponse data;

  public DialogReload() {
  }

  public Dialog getdialog() {
    return dialog;
  }

  public void showDialog(Context context, PackageListResponse data, dialogReloadCallback listener) {

    if (!((Activity) context).isFinishing()) {
      //show dialog
      dialog = new Dialog(context, R.style.AppDialog);
      if (dialog.getWindow() != null) {
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      }
      dialog.setContentView(R.layout.dialog_reload);
      dialog.show();

      this.listener = listener;
      this.data = data;
      this.context = context;

      if (!dialog.isShowing()) {
        dialog.show();
      }

      et_NumberPhone = dialog.findViewById(R.id.et_phone_number);
      radioGroup = dialog.findViewById(R.id.groupRadio);
      btnSubmit = dialog.findViewById(R.id.btn_ok);
      btnSubmit.setVisibility(View.GONE);
      btnSubmit.setOnClickListener(this);

      radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
          RadioButton radioButton = group.findViewById(checkedId);
          if (radioButton.getText().equals(context.getString(R.string.title_provider_xl))) {
            provider = "XL_Payment";
          } else {
            provider = "Telkom_Payment";
          }
          btnSubmit.setVisibility(View.VISIBLE);
        }
      });

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
      case R.id.btn_ok:
        if  (dialog.isShowing()) {
          if (!TextUtils.isEmpty(et_NumberPhone.getText())) {
            if (listener != null) {
              listener.onClick(et_NumberPhone.getText().toString(),
                  provider,
                  data.getPackageID());
            }
          } else {
            et_NumberPhone.setError(context.getString(R.string.error_insert_number));
          }
        }
        break;
    }
  }

  public interface dialogReloadCallback {
    void onClick(String phoneNumber, String provider, String packageID);
  }
}
