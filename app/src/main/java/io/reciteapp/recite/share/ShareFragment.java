package io.reciteapp.recite.share;


import static android.view.View.GONE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.ProgressBarAnimation;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.di.component.ShareFragmentComponent;
import io.reciteapp.recite.di.module.ShareModule;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.share.ShareContract.Presenter;
import io.reciteapp.recite.utils.Utils;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends Fragment implements ShareContract.View {

  @Inject
  Presenter presenter;

  @Inject
  @Named("service")
  NetworkCallWrapper service;
  @BindView(R.id.loadingView)
  AppCompatImageView loadingView;
  @BindView(R.id.tv_ref_code)
  TextView tvRefCode;
  @BindView(R.id.group_all)
  Group groupAll;
  @BindView(R.id.group_error)
  Group groupError;
  Unbinder unbinder;
  @BindView(R.id.btn_share)
  ConstraintLayout btnShare;
  @BindView(R.id.btn_copy)
  ConstraintLayout btnCopy;

  private MainActivity activity;
  private ShareFragmentComponent component;
  private ProgressBarAnimation progressBarAnimation;

  public ShareFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MainActivity) {
      this.activity = (MainActivity) context;
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_share, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    getShareCodeComponent().inject(this);
    init();
    return rootView;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    presenter.unSubscribe();
    unbinder.unbind();
  }

  private ShareFragmentComponent getShareCodeComponent() {
    if (component == null) {
      component = App.getApp()
          .getApplicationComponent().newShareComponent(new ShareModule());
    }
    return component;
  }

  private void init() {
    presenter.setView(this, service);
    setupView();
  }

  private void setupView() {
    progressBarAnimation = new ProgressBarAnimation();

    groupAll.setVisibility(GONE);
    removeErrorLayout();
    removeWait();

    tvRefCode.setVisibility(GONE);
    presenter.checkLogin();
  }

  @Override
  public void showWait() {
    progressBarAnimation.startAnimation(loadingView);
    loadingView.setVisibility(View.VISIBLE);
    tvRefCode.setVisibility(GONE);
  }

  @Override
  public void removeWait() {
    progressBarAnimation.stopAnimation();
    loadingView.setVisibility(GONE);
  }

  @Override
  public void notLoginLayout() {
    tvRefCode.setVisibility(GONE);
    groupAll.setVisibility(View.VISIBLE);
  }

  @Override
  public void onSuccessLayout(String shareCode) {
    groupAll.setVisibility(View.VISIBLE);
    groupError.setVisibility(GONE);

    tvRefCode.setText(shareCode);
  }

  @Override
  public void showErrorLayout() {
    groupAll.setVisibility(GONE);
    groupError.setVisibility(View.VISIBLE);
  }

  @Override
  public void removeErrorLayout() {
    groupError.setVisibility(GONE);
  }

  @Override
  public void showToast(int textR) {
    new Utils().getToast(activity, getString(textR)).show();
  }

  @Override
  public void shareCode(String shareCode) {

    final String message = getString(R.string.msg_share_recite_template, shareCode);

    //Launch shared intent
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TEXT, message + " " + Constants.share_url);
    startActivity(Intent.createChooser(shareIntent, getString(R.string.title_share)));
  }

  @Override
  public void logout() {
    activity.logout();
  }

  @OnClick({R.id.btn_share, R.id.btn_copy, R.id.btn_refresh})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btn_share:
        presenter.shareCode();
        break;
      case R.id.btn_copy:
        presenter.copyCode();
        break;
      case R.id.btn_refresh:
        presenter.getReferralCode();
        break;
    }
  }
}
