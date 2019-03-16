package io.reciteapp.recite.preferences;

import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.FontCache;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.root.BaseActivity;
import java.util.Objects;

public class PreferencesActivity extends BaseActivity {

  @BindView(R.id.toolbar_title)
  TextView toolbarTitle;
  @BindView(R.id.toolbar_title_t)
  TextView toolbarTitleT;
  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    renderView();
  }

  private void renderView() {
    ButterKnife.bind(this);

    //setup toolbar
    setSupportActionBar(toolbar);
    Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    Typeface typeface = FontCache.get("fonts/Raleway-ExtraBold.ttf", this);
    toolbarTitle.setTypeface(typeface);
    toolbarTitleT.setTypeface(typeface);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      Window window = this.getWindow();
      window.addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(ContextCompat.getColor(this, R.color.greyish_brown));
    }

    getFragmentManager().beginTransaction().replace(R.id.frame_layout, new PreferencesFragment()).commit();

  }

  @Override
  protected int getLayoutResourceId() {
    return R.layout.activity_preferences;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  //Tell presenter to logout
  //Finish the activity and restart
  public void logout() {
    new AuthenticationRequest().logout(this);
    new SharedPreferencesManager(this).logout();

    //closed Activity
    finish();
  }

}
