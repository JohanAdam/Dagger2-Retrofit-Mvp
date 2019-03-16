package io.reciteapp.recite.root;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import timber.log.Timber;

/**
 * Extend this activity instead of AppCompatActivity if the activity use AzureService
 */

public abstract class BaseActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutResourceId());
    Timber.d("Check Language");
    new SharedPreferencesManager(this).getLanguage();
  }

  @Override
  protected void onResume() {
    super.onResume();  }

  protected abstract int getLayoutResourceId();
}
