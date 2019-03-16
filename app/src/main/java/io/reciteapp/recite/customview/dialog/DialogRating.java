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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import io.reciteapp.recite.R;

public class DialogRating implements OnClickListener {

  private TextView tv_seekbarValue;
  private SeekBar seekbar;
  private EditText etFeedback;

  private Dialog dialog;
  private DialogRatingCallback listener;

  public DialogRating() {
  }

  public Dialog getDialog() {
    return dialog;
  }

  public void showDialog(Context context, DialogRatingCallback listener) {

    if (!((Activity) context).isFinishing()) {
      //show dialog
      dialog = new Dialog(context, R.style.AppDialog);
      if (dialog.getWindow() != null) {
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      }
      dialog.setContentView(R.layout.dialog_rating_cs);
      dialog.show();

      this.listener = listener;

      tv_seekbarValue = dialog.findViewById(R.id.tv_text_seekbar);
      etFeedback = dialog.findViewById(R.id.et_feedback);
      etFeedback.setVisibility(View.GONE);
      seekbar = dialog.findViewById(R.id.seekbar_rating);
      Button btnOk = dialog.findViewById(R.id.btn_submit);

      int ratingGiven = seekbar.getProgress() + 1;
      tv_seekbarValue.setText(String.valueOf(ratingGiven));

      seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
          tv_seekbarValue.setText(String.valueOf(progress + 1));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
      });

      btnOk.setOnClickListener(this);

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
    if  (dialog.isShowing()) {
      if (listener != null) {
        listener.onClick(seekbar.getProgress() + 1);
      }
      removeDialog();
    }
  }

  public interface DialogRatingCallback {
    void onClick(int rating);
  }
}

