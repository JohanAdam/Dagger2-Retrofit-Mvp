package io.reciteapp.recite.customview.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import io.reciteapp.recite.R;

public class DialogReportAudio implements OnClickListener {

  private EditText et_comment;
  private Button btnSubmit;

  private Dialog dialog;
  private DialogReportCallback callback;

  public DialogReportAudio() {

  }

  public void showDialog(Context context, DialogReportCallback callback) {

    if (!((Activity) context).isFinishing()) {
      //show dialog
      dialog = new Dialog(context, R.style.AppDialog);
      if (dialog.getWindow() != null) {
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      }
      dialog.setContentView(R.layout.dialog_report_audio);
      dialog.setCanceledOnTouchOutside(true);
      dialog.setCancelable(true);
      dialog.show();

      this.callback = callback;

      et_comment = dialog.findViewById(R.id.et_comment);
      btnSubmit = dialog.findViewById(R.id.btn_submit);
      btnSubmit.setEnabled(false);
      btnSubmit.setTextColor(ContextCompat.getColor(context, R.color.disable_item_text));

      btnSubmit.setOnClickListener(this);
      et_comment.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
          //On user changes the text
          if(s.toString().trim().length() == 0) {
            btnSubmit.setEnabled(false);
            btnSubmit.setTextColor(ContextCompat.getColor(context, R.color.disable_item_text));
          } else {
            btnSubmit.setEnabled(true);
            btnSubmit.setTextColor(ContextCompat.getColor(context, R.color.pinkTheme));
          }
        }

        @Override
        public void afterTextChanged(Editable s) {

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
        case R.id.btn_submit:
          callback.onClick(et_comment.getText().toString());
          removeDialog();
          break;
      }
  }


  public interface DialogReportCallback {
    void onClick(String text);
  }

}
