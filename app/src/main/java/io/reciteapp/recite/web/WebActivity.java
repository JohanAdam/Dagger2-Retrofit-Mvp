package io.reciteapp.recite.web;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import io.reciteapp.recite.R;
import io.reciteapp.recite.root.BaseActivity;

public class WebActivity extends BaseActivity {

  @BindView(R.id.toolbar_title)
  TextView toolbarTitle;
  @BindView(R.id.toolbar_title_t)
  TextView toolbarTitleT;
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.progressBar)
  ProgressBar progressBar;
  @BindView(R.id.webview)
  WebView webview;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    progressBar.setMax(100);
    progressBar.setVisibility(View.VISIBLE);
    progressBar.bringToFront();

  }

  @Override
  protected int getLayoutResourceId() {
    return R.layout.activity_web;
  }
}
