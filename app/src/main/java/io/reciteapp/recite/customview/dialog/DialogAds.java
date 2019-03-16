package io.reciteapp.recite.customview.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.GlideApp;

public class DialogAds extends AppCompatActivity {

  @BindView(R.id.tv_title)
  TextView tvTitle;
  @BindView(R.id.tv_desc)
  TextView tvDesc;
  @BindView(R.id.iv_image_ads)
  ImageView ivImageAds;
  @BindView(R.id.btn_submit)
  Button btnSubmit;
  @BindView(R.id.btn_cancel)
  Button btnCancel;
  @BindView(R.id.iv_billplz)
  ImageView ivBillplz;

  private String redirectUrl;

  //TODO fix desc_image too long make view offscreen

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_dialog_ad);
    ButterKnife.bind(this);

    this.setFinishOnTouchOutside(false);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      String message = bundle.getString("message", getString(R.string.app_name));
      String imgUrl = bundle.getString("imgUrl");
      redirectUrl = bundle.getString("redirectUrl", "http://reciteapp.io/");

      tvTitle.setText(getString(R.string.app_name));
//      message = "Lorem ipsum yyuup tup ipaidsidnsaodnsajodnsodnsaondsajdaodsaodsadsahdfsafoudgsfughsufhshfdsofhdsoufhdshsofhsoufhaofhaoufdsdibdsaoudshdsbfsdofbdfdsdkjasdksajdsadnaksljnljnlfnsalkf";
      if (!TextUtils.isEmpty(message)) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          tvDesc.setText(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT));
        } else {
          tvDesc.setText(Html.fromHtml(message));
        }
        tvDesc.setMovementMethod(LinkMovementMethod.getInstance());
      } else {
        tvDesc.setText("");
      }

      GlideApp.with(this)
          .load(imgUrl)
          .placeholder(new ColorDrawable(Color.GRAY))
          .error(R.drawable.logo_title_recite)
          .into(ivImageAds);

      btnCancel.setVisibility(View.GONE);

    }


  }

  @Override
  public void onBackPressed() {
  }

  @OnClick({R.id.iv_image_ads, R.id.btn_submit, R.id.btn_cancel, R.id.iv_billplz})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.iv_image_ads:
        if (!TextUtils.isEmpty(redirectUrl)) {
          openLink(redirectUrl);
        }
        break;
      case R.id.btn_submit:
        finishProcedure();
        break;
      case R.id.btn_cancel:
        finishProcedure();
        break;
      case R.id.iv_billplz:
        openLink(Constants.URL_BILLPLZ);
        break;
    }
  }

  private void finishProcedure() {
    finish();
  }

  private void openLink(String url) {
    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
    CustomTabsIntent customTabsIntent = builder.build();
    customTabsIntent.launchUrl(this, Uri.parse(url));
  }

}
