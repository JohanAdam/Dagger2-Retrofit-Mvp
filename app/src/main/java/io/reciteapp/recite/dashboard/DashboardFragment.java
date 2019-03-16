package io.reciteapp.recite.dashboard;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.liulishuo.magicprogresswidget.MagicProgressCircle;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.ProgressBarAnimation;
import io.reciteapp.recite.customview.dialog.DialogAds;
import io.reciteapp.recite.customview.dialog.DialogUpdateEmail;
import io.reciteapp.recite.dashboard.DashboardContract.Presenter;
import io.reciteapp.recite.dashboard.DashboardContract.View;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.di.component.DashboardFragmentComponent;
import io.reciteapp.recite.di.module.DashboardModule;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.submissionlist.cs.SubmissionListCsFragment;
import io.reciteapp.recite.submissionlist.user.SubmissionListFragment;
import io.reciteapp.recite.surahlist.SurahListFragment;
import io.reciteapp.recite.utils.NotificationUtils;
import io.reciteapp.recite.utils.Utils;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

public class DashboardFragment extends Fragment implements View {

  @Inject
  Presenter presenter;

  @Inject
  @Named("service")
  NetworkCallWrapper service;

  @BindView(R.id.CircleProgressBar)
  MagicProgressCircle pb_reciteTimeProgressBar;
  @BindView(R.id.progressBar)
  AppCompatImageView pb_loadingBar;
  @BindView(R.id.reciteTime)
  TextView tv_ReciteTime;
  @BindView(R.id.tv_last_sent)
  TextView tv_LastSent;
  @BindView(R.id.tv_last_sent_subs)
  TextView tv_LastSentSubs;
  @BindView(R.id.tv_total)
  TextView tv_Total;
  Unbinder unbinder;

  //Dialog
  private DialogUpdateEmail dialogUpdateEmail = null;

  //Get dashboard component
  private DashboardFragmentComponent component;
  //mark true if dashboard call is in progress
  private boolean isCall = false;
  //Fragment Parent activity
  private MainActivity activity;
  private Utils utils;
  private ProgressBarAnimation progressBarAnimation;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MainActivity) {
      this.activity = (MainActivity) context;
    }
  }

  @Nullable
  @Override
  public android.view.View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    //Root view of fragment;
    android.view.View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    //Inject service and presenter
    getDashboardComponent().inject(this);
    init();
    return rootView;
  }

  @Override
  public void onResume() {
    super.onResume();
    Timber.d("onResume");
    init();
    new NotificationUtils(getActivity()).clearNotification(Constants.OPEN_DEFAULT_ID);
    presenter.checkNotification();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    Timber.e("unSubscribe");
    //cancel all call from this fragment
    isCall = false;
    presenter.unSubscribe();
    unbinder.unbind();
  }

  private DashboardFragmentComponent getDashboardComponent() {
    if (component == null) {
      component = App.getApp().getApplicationComponent()
          .newDashboardFragmentComponent(new DashboardModule());
    }
    return component;
  }

  private void init() {
    presenter.setView(this, service);

    utils = new Utils();

    //Set progress bar animation
    progressBarAnimation = new ProgressBarAnimation();

    notLoginView();

    Timber.d("init %s", isCall);
    if (presenter.getLoginStatus()) {
      activity.updateLogItem();
      if (!isCall) {
        presenter.getDashboard();
      }
    }
  }

  @Override
  public void showWait() {
    isCall = true;
    progressBarAnimation.startAnimation(pb_loadingBar);
    pb_loadingBar.setVisibility(android.view.View.VISIBLE);
  }

  @Override
  public void removeWait() {
    isCall = false;
    progressBarAnimation.stopAnimation();
    pb_loadingBar.setVisibility(android.view.View.GONE);
  }

  @Override
  public void showLoadingDialog() {
    activity.showLoadingDialog();
  }

  @Override
  public void removeLoadingDialog() {
    activity.removeLoadingDialog();
  }

  @Override
  public void updateDrawerItem(int position, boolean isEnable) {
    activity.updateDrawerItem(position, isEnable);
  }

  @Override
  public void changeTextCount(int lastSeen, int totalSub) {
    if (isVisible()) {
      int initialValue = 0;

      if (lastSeen >= 24) {
        tv_LastSentSubs.setText(R.string.msg_days);
      } else {
        tv_LastSentSubs.setText(R.string.msg_hours);
      }

      ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, lastSeen);
      valueAnimator.setDuration(1500);

      valueAnimator.addUpdateListener(
          valueAnimator12 -> {
            if (isVisible()){
              tv_LastSent.setText(valueAnimator12.getAnimatedValue().toString());
            }
          });
      valueAnimator.start();

      valueAnimator = ValueAnimator.ofInt(initialValue, totalSub);
      valueAnimator.setDuration(1500);

      valueAnimator.addUpdateListener(
          valueAnimator1 -> {
            if (isVisible()){
              tv_Total.setText(valueAnimator1.getAnimatedValue().toString());
            }
          });
      valueAnimator.start();
    }
  }

  @Override
  public void setReciteTime(String time) {
    tv_ReciteTime.setText(time);
  }

  @Override
  public void updateCircleProgressBar(int remainTime, int totalTime) {
    float time = (float) remainTime / totalTime;
    pb_reciteTimeProgressBar.setSmoothPercent(time);
  }

  @Override
  public void notLoginView() {
    //Set View To 0
    tv_ReciteTime.setText(R.string.dummy_time);
    tv_LastSent.setText(R.string.dummy_0);
    tv_LastSentSubs.setText(R.string.msg_minutes);
    tv_Total.setText(R.string.dummy_0);
    pb_reciteTimeProgressBar.setSmoothPercent(0, 0);
    pb_loadingBar.setVisibility(android.view.View.GONE);

    //disable referral item
    updateDrawerItem(1, false);
    //disable redemption item
    updateDrawerItem(2, false);
    //disable reload item
    updateDrawerItem(3, false);
    //disable cs test item
    updateDrawerItem(4, false);
    //update log item
    activity.updateLogItem();
  }

  @Override
  public void showSnackBar(int stringId) {
    activity.showSnackBar(stringId);
  }

  @Override
  public void showInfoDialog(int title, int message) {
    activity.showInfoDialog(title, message);
  }

  @Override
  public void showInfoDialog(String title, String message) {
    activity.showInfoDialog(title, message);
  }

  @Override
  public void showShareDialog(int value) {

    Timber.d("referral time value is %s", value);
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

    String text = getString(R.string.msg_referral_time_desc, time, referralTime);

    activity.showShareDialog(getString(R.string.title_congratulations), text);
  }

  @Override
  public void showErrorDialog(int responseCode, boolean isKick) {
    activity.showErrorDialog(responseCode, isKick);
  }

  @Override
  public void showUpdateEmailDialog() {
    if (dialogUpdateEmail == null) {
      dialogUpdateEmail = new DialogUpdateEmail();
      dialogUpdateEmail.showDialog(activity, email -> {
        Timber.d("onClick Email");
        presenter.patchEmail(email);
        dialogUpdateEmail = null;
      });
    }
  }

  //TODO make this Ads dialog and Sponsor dialog as one method
  @Override
  public void showAdsDialog(String message, String imageUrl, String redirectUrl) {
    Intent intent = new Intent(activity, DialogAds.class);
    intent.putExtra("message", message);
    intent.putExtra("imgUrl", imageUrl);
    intent.putExtra("redirectUrl", redirectUrl);
    startActivity(intent);
  }

  @Override
  public void openSurahList() {
    Fragment fragment = new SurahListFragment();
    activity.switchFragmentAddBackstack(fragment, Constants.TAG_OTHERS_FRAGMENT);
  }

  @Override
  public void openSubmissionList() {
    Fragment fragment = new SubmissionListFragment();
    activity.switchFragmentAddBackstack(fragment, Constants.TAG_OTHERS_FRAGMENT);
  }

  @Override
  public void openSubmissionListCs() {
    Fragment fragment = new SubmissionListCsFragment();
    activity.switchFragmentAddBackstack(fragment, Constants.TAG_OTHERS_FRAGMENT);
  }

  @Override
  public void openLoginActivity() {
    activity.openLoginActivity();
  }

  @Override
  public void logout() {
    activity.logout();
  }

  @OnClick({R.id.card_validate, R.id.card_listen, R.id.reciteTime})
  public void onClick(android.view.View view) {
    switch (view.getId()) {
      case R.id.card_validate:
        presenter.openSurahList();
        break;
      case R.id.card_listen:
        presenter.openSubmissionList();
        break;
      case R.id.reciteTime:
        Timber.d("Token copied");
        utils.copyToClipboard(presenter.getToken());
        utils.getToast(activity, "Token copy to clipboard :D").show();
        break;
    }
  }
}
