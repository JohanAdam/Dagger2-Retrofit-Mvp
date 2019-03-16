package io.reciteapp.recite.csprofile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reciteapp.recite.R;
import io.reciteapp.recite.csprofile.CsProfileContract.Presenter;
import io.reciteapp.recite.customview.ProgressBarAnimation;
import io.reciteapp.recite.customview.animation.RecyclerViewAnimation;
import io.reciteapp.recite.data.GlideApp;
import io.reciteapp.recite.data.model.CsProfileResponse;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.di.component.CsProfileFragmentComponent;
import io.reciteapp.recite.di.module.CsProfileModule;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.root.App;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * A simple {@link Fragment} subclass.
 */
public class CsProfileFragment extends Fragment implements CsProfileContract.View {

  @Inject
  Presenter presenter;

  @Inject
  @Named("service")
  NetworkCallWrapper service;
  @BindView(R.id.avatarView)
  CircleImageView ivPhoto;
  @BindView(R.id.name_cs)
  TextView tvName;
  @BindView(R.id.progressBar)
  AppCompatImageView progressBar;
  @BindView(R.id.tv_reviews_count)
  TextView tvReviewsCount;
  @BindView(R.id.tv_rating_count)
  TextView tvRatingCount;
  @BindView(R.id.tv_response_time)
  TextView tvResponseTime;
  @BindView(R.id.affliation_data_tv)
  AppCompatTextView tvAffiliation;
  @BindView(R.id.certification_data_rv)
  RecyclerView certificationRecyclerview;
  @BindView(R.id.main_layout)
  NestedScrollView mainLayout;
  Unbinder unbinder;

  private MainActivity activity;
  private CsProfileFragmentComponent component;
  private ProgressBarAnimation progressBarAnimation;

  public CsProfileFragment() {
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
    View rootView = inflater.inflate(R.layout.fragment_cs_profile, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    getCsProfileComponent().inject(this);
    init();
    return rootView;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    presenter.unSubscribe();
    unbinder.unbind();
  }

  private CsProfileFragmentComponent getCsProfileComponent() {
    if (component == null) {
      component = App.getApp().getApplicationComponent()
          .newCsProfileFragmentComponent(new CsProfileModule());
    }
    return component;
  }

  /**
   * Initialized and setup view
   */
  private void init() {
    presenter.setView(this, service);
    setupView();
  }

  /**
   * Setup first view
   */
  private void setupView() {

    Bundle bundle = this.getArguments();
    if (bundle != null) {
      if (!TextUtils.isEmpty(bundle.getString("id"))) {
        presenter.setId(bundle.getString("id"));
      }
    }

    progressBarAnimation = new ProgressBarAnimation();

    ivPhoto.setImageResource(R.drawable.ic_logo);
    tvName.setText(R.string.title_credible_source);

    tvReviewsCount.setText(R.string.dummy_0);
    tvRatingCount.setText(R.string.dummy_0);
    tvResponseTime.setText(R.string.dummy_0);

    tvAffiliation.setText("");

    certificationRecyclerview.setVisibility(View.GONE);
    certificationRecyclerview.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
    certificationRecyclerview.setLayoutManager(linearLayoutManager);

    presenter.getCsProfile();
  }

  @Override
  public void showWait() {
    progressBarAnimation.startAnimation(progressBar);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override
  public void removeWait() {
    progressBarAnimation.stopAnimation();
    progressBar.setVisibility(View.GONE);
  }

  @SuppressLint("SetTextI18n")
  @Override
  public void setData(CsProfileResponse response) {
    GlideApp.with(this)
        .load(response.getPhoto())
        .placeholder(new ColorDrawable(Color.GRAY))
        .error(R.drawable.logo_title_recite)
        .into(ivPhoto);
    tvName.setText(response.getName());

    tvReviewsCount.setText(String.valueOf(response.getAssessment()));
    tvRatingCount.setText(response.getRating());
    if (TimeUnit.MILLISECONDS.toHours((long) response.getResponseTime()) < 24) {
      if (TimeUnit.MILLISECONDS.toMinutes((long) response.getResponseTime()) < 60) {
        if (TimeUnit.MILLISECONDS.toSeconds((long) response.getResponseTime()) < 60) {
          tvResponseTime.setText("~" + String.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) response.getResponseTime())) + " " + getString(R.string.msg_seconds));
        } else {
          tvResponseTime.setText("~" + String.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) response.getResponseTime())) + " " + getString(R.string.msg_minutes));
        }
      } else {
        tvResponseTime.setText("~" + String.valueOf(TimeUnit.MILLISECONDS.toHours((long) response.getResponseTime())) + " " + getString(R.string.msg_hours));
      }
    } else {
      //more
      tvResponseTime.setText("~24" + getString(R.string.msg_hours));
    }

    tvAffiliation.setText(response.getAffiliation());

    certificationRecyclerview.setVisibility(View.VISIBLE);
    CsProfileCertificationAdapter mAdapter = new CsProfileCertificationAdapter(activity, response.getCertification());
    certificationRecyclerview.setItemAnimator(new DefaultItemAnimator());
    certificationRecyclerview.setAdapter(mAdapter);
    new RecyclerViewAnimation().rerunLayoutAnimation(certificationRecyclerview);
  }

  @Override
  public void showErrorLayout() {
    activity.showSnackBarPersistantWithAction(R.string.error_default, R.string.action_refresh,
        () -> presenter.getCsProfile());
  }


  @Override
  public void logout() {
    activity.logout();
  }
}
