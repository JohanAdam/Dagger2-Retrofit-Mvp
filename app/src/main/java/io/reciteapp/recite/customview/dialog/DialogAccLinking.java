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
import com.google.firebase.auth.GoogleAuthProvider;
import io.reciteapp.recite.R;
import io.reciteapp.recite.root.App;

public class DialogAccLinking implements OnClickListener {

  public Dialog dialog;
  public DialogAccLinkingCallback callback;
  public Button btn_facebook, btn_google, btn_link;
  public TextView tvTitle, tvDesc;

  public DialogAccLinking() {
  }

  public Dialog getDialogAccLinking() {
    return dialog;
  }

  public void showDialog(Context context, String provider, DialogAccLinking.DialogAccLinkingCallback listener) {

    if (!((Activity) context).isFinishing()) {
      //show dialog
      dialog = new Dialog(context, R.style.AppDialog);
      if (dialog.getWindow() != null) {
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      }
      dialog.setContentView(R.layout.dialog_acc_linking);
      dialog.setCanceledOnTouchOutside(false);
      dialog.setCancelable(false);
      dialog.show();

      this.callback = listener;

      tvTitle = dialog.findViewById(R.id.tv_title);
      tvDesc = dialog.findViewById(R.id.tv_desc);
      btn_facebook = dialog.findViewById(R.id.btn_sign_in_facebook);
      btn_google = dialog.findViewById(R.id.btn_sign_in_google);
      btn_link = dialog.findViewById(R.id.btn_link_account);

      tvTitle.setText(R.string.title_link_account);

      if (provider.equals(GoogleAuthProvider.PROVIDER_ID)) {
        //Having collision with google, because use previously login with facebook
        provider = App.getApp().getString(R.string.title_facebook);
        btn_google.setVisibility(View.GONE);
      } else {
        //Having collision with facebook, because user previously login with google
        provider = App.getApp().getString(R.string.title_google);
        btn_facebook.setVisibility(View.GONE);
      }

      tvDesc.setText(App.getApp().getString(R.string.msg_exist_email_linking, provider));
      btn_facebook.setOnClickListener(this);
      btn_google.setOnClickListener(this);
      btn_link.setOnClickListener(this);

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
      case R.id.btn_sign_in_facebook:
        if  (dialog.isShowing()) {
          callback.onClick(R.id.btn_sign_in_facebook);
          removeDialog();
        }
        break;
      case R.id.btn_sign_in_google:
        if (dialog.isShowing()) {
          callback.onClick(R.id.btn_sign_in_google);
          removeDialog();
        }
        break;
      case R.id.btn_link_account:
        if (dialog.isShowing()) {
          callback.onClick(R.id.btn_link_account);
          removeDialog();
        }
        break;
      default:
        removeDialog();
        break;
    }
  }

  public interface DialogAccLinkingCallback {
    void onClick(int btnClick);
  }
}
