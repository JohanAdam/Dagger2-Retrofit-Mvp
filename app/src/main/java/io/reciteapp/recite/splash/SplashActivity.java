package io.reciteapp.recite.splash;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import io.reciteapp.recite.R;
import io.reciteapp.recite.main.MainActivity;

public class SplashActivity extends MainActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Window window = this.getWindow();
    //Setup the status bar for Android Kitkat
    if (VERSION.SDK_INT == VERSION_CODES.KITKAT) {
      window.clearFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
    //Setup the status bar for Android Kitkat above
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setNavigationBarColor(ContextCompat.getColor(this, R.color.slate));
      window.setStatusBarColor(ContextCompat.getColor(this, R.color.slate));
    }

    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    finish();
  }
}
