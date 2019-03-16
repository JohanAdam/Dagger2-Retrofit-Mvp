package io.reciteapp.recite.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.call.NotificationTokenCall.PostNotificationCallback;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.root.ApplicationComponent;
import io.reciteapp.recite.utils.NotificationUtils;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

public class ReciteFirebaseMessagingService extends FirebaseMessagingService {

  @Inject
  @Named("service")
  NetworkCallWrapper service;

  private ApplicationComponent component;

  @Override
  public void onCreate() {
    super.onCreate();
    Timber.d("onCreate reciteFirebaseMessagingService");
    getApplicationComponent().inject(this);
  }

  private ApplicationComponent getApplicationComponent() {
    if (component == null) {
      component = App.getApp().getApplicationComponent();
    }
    return component;
  }

  /**
   * Called if InstanceID token is updated. This may occur if the security of
   * the previous token had been compromised. Note that this is called when the InstanceID token
   * is initially generated so this is where you would retrieve the token.
   */
  @Override
  public void onNewToken(String token) {
    super.onNewToken(token);
    Timber.d("Refreshed notification token %s", token);

    // If you want to send messages to this application instance or
    // manage this apps subscriptions on the server side, send the
    // Instance ID token to your app server.
    sendRegistrationToServer(token);
  }

  private void sendRegistrationToServer(String token) {
    Timber.d("sendRegistration notification token to server");

    String authenticationToken = new SharedPreferencesManager(this).loadString(Constants.PREF_TOKEN);
    if (!TextUtils.isEmpty(authenticationToken)) {
      service.postNotificationToken(authenticationToken, token,
          new PostNotificationCallback() {
            @Override
            public void onSuccess() {
              Timber.d("onSuccess sendRegistrationToServer");
            }

            @Override
            public void onError(int response) {
              Timber.e("onError sendRegistrationToServer %s", response);
            }
          });
    }

  }

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);
    Timber.d("onMessageReceived");

    if (remoteMessage.getData() != null) {
      Timber.d("remoteMessage receive not null");
      Map<String, String> data = remoteMessage.getData();
      String myCustomKey = data.get("ayat");
      Timber.d("myCustomKeys is %s", myCustomKey);
      showNotification(remoteMessage.getData());
    }

  }

  PendingIntent pendingIntent;
  Intent intent;
  Bundle bundle;
  int notificationId = 1;
  private void showNotification(Map<String, String> data) {
    Timber.d("showNotification");

    String msg = data.get("body"); //Body notification message
    String title = data.get("title"); //Submission, Rate, Reviewed, Reload, Time, ReferralTime
    String surah = data.get("surah"); //Name of surah
    String ayat = data.get("ayat"); //Ayat id

    Timber.d("Msg " + msg + " title " + title + " surah " + surah + " ayat " + ayat);

    if (title.equals("submission")) {
      //New submission (There is new submission)
      intent = new Intent(getApplicationContext(), MainActivity.class);

      bundle = new Bundle();
      bundle.putString("fragment", Constants.OPEN_FRAGMENT_SUBMISSION);
      intent.putExtras(bundle);

      pendingIntent = PendingIntent.getActivity(getApplicationContext(),
          0,
          intent,
          PendingIntent.FLAG_UPDATE_CURRENT);

      title = getResources().getString(R.string.notification_title_new_submission);
      notificationId = Constants.OPEN_FRAGMENT_SUBMISSION_ID;

    } else if (title.equals("rate") | title.equals("review")) {
      //New rating (Your Submission has been rated)
      //New review (Your Submission has been reviewed)
      intent = new Intent(getApplicationContext(), MainActivity.class);

      bundle = new Bundle();
      bundle.putString("fragment", Constants.OPEN_FRAGMENT_HISTORY);
      bundle.putString("surah_name", surah);
      bundle.putString("ayat_id", ayat);
      intent.putExtras(bundle);

      pendingIntent = PendingIntent.getActivity(getApplicationContext(),
          0,
          intent,
          PendingIntent.FLAG_UPDATE_CURRENT);

      if (title.equals("rate")) {
        title = getResources().getString(R.string.notification_title_new_rating);
      } else {
        title = getResources().getString(R.string.notification_title_new_review);
      }
      notificationId = Constants.OPEN_FRAGMENT_HISTORY_ID;

    } else if (title.equals("reload") | title.equals("time") | title.equals("referraltime")) {
      //Reload success
      //Message from Recite
      //Free recite time from Referral Programme

      if (title.equals("time")) {
        new SharedPreferencesManager(getApplicationContext()).saveString(Constants.PREF_MESSAGE, msg);
      } else if (title.equals("referraltime")) {
        new SharedPreferencesManager(getApplicationContext()).saveInt(Constants.PREF_REFERRAL_TIME, Integer.parseInt(msg));
      }

      if (title.equals("referraltime")) {

        long value = Long.parseLong(msg);
        long referralTime = 0;
        String time = getResources().getString(R.string.msg_seconds);

        if (value >= 0 && value < 60000) {
          //seconds
          time = getResources().getString(R.string.msg_seconds);
          referralTime = TimeUnit.MILLISECONDS.toSeconds(value);
        } else if (value >= 60000 && value < 3600000) {
          //minutes
          time = getResources().getString(R.string.msg_minutes);
          referralTime = TimeUnit.MILLISECONDS.toMinutes(value);
        } else if (value >= 3600000) {
          //hours
          time = getResources().getString(R.string.msg_hours);
          referralTime = TimeUnit.MILLISECONDS.toHours(value);
        }

        msg = getString(R.string.notification_message_referral_time, time, referralTime);
      }

      pendingIntent = PendingIntent.getActivity(getApplicationContext(),
          0,
          new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
          PendingIntent.FLAG_UPDATE_CURRENT);

      if (title.equals("reload")) {
        title = getResources().getString(R.string.notification_title_reload_success);
      } else {
        title = getResources().getString(R.string.app_name);
      }

      notificationId = Constants.OPEN_DEFAULT_ID;
    }

    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());

    //Build notification
    if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
      Notification.Builder mNotificationBuilder = notificationUtils.getAndroid_O_Notification(title,
          msg,
          getApplicationContext(),
          pendingIntent);
      notificationUtils.getManager().notify(notificationId, mNotificationBuilder.build());
    } else {
      Notification mNotification = notificationUtils.getAndroid_Notification(title,
          msg,
          getApplicationContext(),
          pendingIntent);
      notificationUtils.getManager().notify(notificationId, mNotification);
    }
  }

}
