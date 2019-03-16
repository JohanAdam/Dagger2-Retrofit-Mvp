package io.reciteapp.recite.customview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.model.RegisteredUserProfileResponse;
import io.reciteapp.recite.utils.Utils;
import timber.log.Timber;

public class DialogRegister implements OnClickListener {

  private Dialog dialog;
  private DialogRegisterCallback listener;
  private EditText et_fn, et_ln, et_email, et_contactNumber, et_nationality, et_address;
  private Button btn_male, btn_female, btn_register;
  private NestedScrollView scrollView;
  private Context context;
  private Utils utils;

  // 1 is male, 2 is female
  private int genderChosen = 0;

  public DialogRegister() {
    utils = new Utils();
  }

  public Dialog getDialog() {
    return dialog;
  }

  public void showDialog(Context context, DialogRegisterCallback listener) {

    dialog = new Dialog(context, R.style.AppDialog);
    if (dialog.getWindow() != null) {
      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    dialog.setContentView(R.layout.dialog_register);
    dialog.setCanceledOnTouchOutside(false);
    dialog.setCancelable(false);
    dialog.show();

    this.context = context;
    this.listener = listener;

    init();
  }

  private void init(){
    et_fn = dialog.findViewById(R.id.et_firstname);
    et_ln = dialog.findViewById(R.id.et_lastname);
    et_email = dialog.findViewById(R.id.et_email);
    et_contactNumber = dialog.findViewById(R.id.et_contactNumber);
    et_nationality = dialog.findViewById(R.id.et_nationality);
    et_address = dialog.findViewById(R.id.et_address);
    btn_male = dialog.findViewById(R.id.btn_male);
    btn_female = dialog.findViewById(R.id.btn_female);
    btn_register = dialog.findViewById(R.id.btn_register);
    scrollView = dialog.findViewById(R.id.main_layout);

    btn_male.setOnClickListener(this);
    btn_female.setOnClickListener(this);
    btn_register.setOnClickListener(this);
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
      case R.id.btn_male:
        Timber.d("btn_male click");
        genderChosen = 1;
        btn_male.setPressed(true);
        btn_male.setEnabled(false);

        //unPressed other button
        btn_female.setPressed(false);
        btn_female.setEnabled(true);
        break;
      case R.id.btn_female:
        Timber.d("btn_female click");
        genderChosen = 2;
        btn_female.setPressed(true);
        btn_female.setEnabled(false);

        //unPressed other button
        btn_male.setPressed(false);
        btn_male.setEnabled(true);
        break;
      case R.id.btn_register:
        Timber.d("btn_register click");

        if (TextUtils.isEmpty(et_fn.getText().toString())) {
          if (context != null) {
            et_fn.setError(context.getString(R.string.msg_please_insert_valid_name));
            scrollView.scrollTo(0, scrollView.getTop());
          }
        }

        if (TextUtils.isEmpty(et_ln.getText().toString())) {
          if (context != null) {
            et_ln.setError(context.getString(R.string.msg_please_insert_valid_name));
            scrollView.scrollTo(0, scrollView.getTop());
          }
        }

        if (TextUtils.isEmpty(et_email.getText().toString()) || !Utils.isValidEmail(et_email.getText().toString())) {
          if (context != null) {
            et_email.setError(context.getString(R.string.msg_please_insert_valid_email));
            scrollView.scrollTo(0, scrollView.getTop());
          }
        }

        if (TextUtils.isEmpty(et_contactNumber.getText().toString())) {
          if (context != null) {
            et_contactNumber.setError(context.getString(R.string.msg_please_insert_valid_number));
            scrollView.scrollTo(0, scrollView.getBottom());
          }
        }

        if (TextUtils.isEmpty(et_nationality.getText().toString())) {
          if (context != null) {
            et_nationality.setError(context.getString(R.string.msg_please_insert_valid_nationality));
            scrollView.scrollTo(0, scrollView.getBottom());
          }
        }

        if (TextUtils.isEmpty(et_address.getText().toString())) {
          if (context != null) {
            et_address.setError(context.getString(R.string.msg_please_insert_valid_address));
            scrollView.scrollTo(0, scrollView.getBottom());
          }
        }

        if (genderChosen == 0) {
          if (context != null) {
            utils.getToast(context, context.getString(R.string.msg_please_choose_gender)).show();
          }
        }

        if (!TextUtils.isEmpty(et_fn.getText().toString()) && !TextUtils.isEmpty(et_ln.getText().toString())
            && !TextUtils.isEmpty(et_email.getText().toString()) && !TextUtils.isEmpty(et_contactNumber.getText().toString())
            && !TextUtils.isEmpty(et_nationality.getText().toString()) && !TextUtils.isEmpty(et_address.getText().toString())
            && genderChosen != 0) {

          RegisteredUserProfileResponse dataModel = new RegisteredUserProfileResponse();
          dataModel.setFirstname(et_fn.getText().toString());
          dataModel.setLastname(et_ln.getText().toString());
          dataModel.setEmail(et_email.getText().toString());
          dataModel.setContactnumber(et_contactNumber.getText().toString());
          dataModel.setNationality(et_nationality.getText().toString());
          dataModel.setAddress(et_address.getText().toString());
          dataModel.setGender(genderChosen);

          //return value to register
          listener.onSent(dataModel);

        } else {
          utils.getToast(context, context.getString(R.string.msg_please_enter_complete_details)).show();
        }

        break;
      default:
        Timber.d("default click");
        genderChosen = 0;
        btn_male.setPressed(false);
        btn_male.setEnabled(true);

        //unPressed other button
        btn_female.setPressed(false);
        btn_female.setEnabled(true);
        break;

    }
  }

  public interface DialogRegisterCallback {

    void onSent(RegisteredUserProfileResponse dataModel);
  }

}
