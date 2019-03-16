package io.reciteapp.recite.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.root.App;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

public class Utils {

  public static int checkErrorCode(Exception e) {
    int responseCode;
    try {
      String message = e.getMessage();
      String str = message.replaceAll("\\D+", "");
      responseCode = Integer.parseInt(str);
      return responseCode;
    } catch (NumberFormatException ex) {
      ex.printStackTrace();
      responseCode = Constants.RESPONSE_CODE_FAILED;
      return responseCode;
    }
  }

  //Check for valid email
  public static boolean isValidEmail(CharSequence target) {
    return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target)
        .matches();
  }

  public void hideKeyboard(Activity activity) {
    View view = activity.getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
      assert imm != null;
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  public Snackbar getSnackBar(View context, String message) {
    return Snackbar.make(context, message, Snackbar.LENGTH_SHORT);
  }

  public interface SnackBarCallback {
    void onClick();
  }

  public Snackbar getSnackBarWithAction(View view, String message, String buttonTitle, SnackBarCallback callback) {

    Snackbar snackBar = Snackbar.make(view,
        message, Snackbar.LENGTH_INDEFINITE);
    snackBar.setAction(buttonTitle, v -> callback.onClick());

    return snackBar;
  }

  public Toast getToast(Context context, String message){
    return Toast.makeText(context, message, Toast.LENGTH_SHORT);
  }

  public String trimSurahNameToFileName(String surahName) {

    String surahNameEditOne = surahName.replace(" " ,"");
    String surahNameEditTwo = surahNameEditOne.replace("`", "_");
    String surahNameLastEdit = surahNameEditTwo.trim();

    return surahNameLastEdit;
  }


  /**
   * Convert String time format MM:SS to Seconds
   * @param time time MM:SS
   * @return seconds in long
   */
  public long convertTimerStringFormatToSeconds(String time){
    String[] units = time.split(":"); //will break the string up into an array
    int minutes = Integer.parseInt(units[0]); //first element
    int seconds = Integer.parseInt(units[1]); //second element
    return (long) (60 * minutes + seconds);
  }

  /**
   * Convert milliseconds time to String time format HH:MM:SS
   * Or MM:SS if no Hours available
   */
  public String millisecondsToTimerStringFormat(long milliseconds) {
    String finalTimerString = "";
    String secondsString;

    // Convert total duration into time
    int hours = (int) (milliseconds / (1000 * 60 * 60));
    int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
    int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
    // Add hours if there
    if (hours > 0) {
      finalTimerString = hours + ":";
    }

    // Prepending 0 to seconds if it is one digit
    if (seconds < 10) {
      secondsString = "0" + seconds;
    } else {
      secondsString = "" + seconds;
    }

    finalTimerString = finalTimerString + minutes + ":" + secondsString;

    // return timer string
    return finalTimerString;
  }

  //This will return true if user selected in 85%, or false if user selected in 15%.
  public boolean randomizerDialogPopup() {
    Random r = new Random();
    int value = r.nextInt(100);

    Timber.e("Return randomizer %s", value);
    return value < 70;
  }

  /**
   * Copy to clipboard
   */
  public void copyToClipboard(String content) {
    ClipboardManager clipboard = (ClipboardManager) App.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData clip = ClipData.newPlainText("text", content + " " + Constants.share_url);
    assert clipboard != null;
    clipboard.setPrimaryClip(clip);
  }

  /**
   * Change seconds to timestamp format.
   * @param seconds in String.
   * @return string timestamp (3 seconds ago)
   */
  public String secondsToTimeStamp(int seconds) {
    StringBuilder builder = new StringBuilder(100);
    if (seconds >= 60) {
      if (TimeUnit.SECONDS.toMinutes(seconds) >= 60) {
        if (TimeUnit.SECONDS.toHours(seconds) >= 12) {
          //Days
          builder.append(TimeUnit.SECONDS.toDays(seconds));
          builder.append(" ");
          builder.append(App.getApp().getResources().getString(R.string.msg_days));
        } else {
          //Hours
          builder.append(TimeUnit.SECONDS.toHours(seconds));
          builder.append(" ");
          builder.append(App.getApp().getResources().getString(R.string.msg_hours));
        }
      } else {
        //Minutes
        builder.append(TimeUnit.SECONDS.toMinutes(seconds));
        builder.append(" ");
        builder.append(App.getApp().getResources().getString(R.string.msg_minutes));
      }
    } else {
      //Seconds
      builder.append(seconds);
      builder.append(" ");
      builder.append(App.getApp().getResources().getString(R.string.msg_seconds));
    }
    builder.append(" ");
    builder.append("ago");
    return builder.toString();
  }

}
