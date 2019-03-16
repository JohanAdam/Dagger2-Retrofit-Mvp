package io.reciteapp.recite.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import io.reciteapp.recite.R;

public class NotificationUtils extends ContextWrapper {

  public static final String CHANNEL_ID = "io.reciteapp.recite.noti_receive";
  public static final String CHANNEL_NAME = "Notification Receive";
  private NotificationManager notificationManager;

  public NotificationUtils(Context context) {
    super(context);
    createChannel();
  }

  private void createChannel() {
    // create android channel
    NotificationChannel androidChannel;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      androidChannel = new NotificationChannel(CHANNEL_ID,
          CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
      androidChannel.setDescription("Notification when receive new submission or submission has been reviewed.");
      // Sets whether notifications posted to this channel should display notification lights
      androidChannel.enableLights(true);
      // Sets whether notification posted to this channel should vibrate.
      androidChannel.enableVibration(true);
      // Sets the notification light color for notifications posted to this channel
      androidChannel.setLightColor(Color.MAGENTA);
      // Sets whether notifications posted to this channel appear on the lockscreen or not
      androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

      getManager().createNotificationChannel(androidChannel);
    }
  }

  public NotificationManager getManager() {
    if (notificationManager == null) {
      notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
    return notificationManager;
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  public Notification.Builder getAndroid_O_Notification(String title, String body, Context
      context, PendingIntent intent) {
    return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(body)
        .setShowWhen(true)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentIntent(intent)
        .setAutoCancel(true);
  }

  public Notification getAndroid_Notification(String title, String body, Context context,
      PendingIntent intent) {
    return new NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_logo)
        .setDefaults(Notification.DEFAULT_ALL)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
        .setShowWhen(true)
        .setContentTitle(title)
        .setContentText(body)
        .setContentIntent(intent)
        .build();
  }

  public void clearNotification(int notificationId){
    NotificationManager mNotificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    assert mNotificationManager != null;
    mNotificationManager.cancel(notificationId);
  }

}
