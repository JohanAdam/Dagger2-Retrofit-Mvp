package io.reciteapp.recite.progress;


import static android.view.View.GONE;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.ProgressBarAnimation;
import io.reciteapp.recite.customview.animation.RecyclerViewAnimation;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.di.component.ProgressFragmentComponent;
import io.reciteapp.recite.di.module.ProgressModule;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.data.model.HistoryResponse;
import io.reciteapp.recite.progress.ProgressContract.Presenter;
import io.reciteapp.recite.root.App;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgressFragment extends Fragment implements ProgressContract.View {

  @Inject
  Presenter presenter;

  @Inject
  @Named("service")
  NetworkCallWrapper service;

  @BindView(R.id.group_error)
  Group groupError;
  @BindView(R.id.btn_refresh)
  Button btnRefresh;
  @BindView(R.id.loadingView)
  AppCompatImageView loadingView;
  @BindView(R.id.group_empty)
  Group groupEmpty;
  @BindView(R.id.tv_empty_message)
  TextView tv_empty_msg;
  @BindView(R.id.recyclerView)
  RecyclerView recyclerView;
  @BindView(R.id.main_layout)
  ConstraintLayout mainLayout;
  Unbinder unbinder;

  private MainActivity activity;
  private ProgressFragmentComponent component;
  private ProgressBarAnimation progressBarAnimation;

  public ProgressFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MainActivity) {
      activity = (MainActivity) context;
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_history_list, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    getProgressComponent().inject(this);
    init();
    return rootView;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    presenter.unSubscribe();
  }

  private ProgressFragmentComponent getProgressComponent() {
    if (component == null) {
      component = App.getApp().getApplicationComponent()
          .newProgressFragmentComponent(new ProgressModule());
    }
    return component;
  }

  private void init() {
    presenter.setView(this, service);
    setupView();
    if (presenter.getLoginStatus()) {
      presenter.getHistoryList();
    } else {
      showNoResultLayout();
    }
  }

  private void setupView() {

    progressBarAnimation = new ProgressBarAnimation();

    removeWait();
    removeNoResultLayout();
    removeErrorLayout();

    recyclerView.setVisibility(GONE);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
    recyclerView.setLayoutManager(linearLayoutManager);
  }

  @Override
  public void showWait() {
    progressBarAnimation.startAnimation(loadingView);
    loadingView.setVisibility(View.VISIBLE);
  }

  @Override
  public void removeWait() {
    progressBarAnimation.stopAnimation();
    loadingView.setVisibility(GONE);
  }

  @Override
  public void setHistoryListToRv(ArrayList<HistoryResponse> responses) {
    recyclerView.setVisibility(View.VISIBLE);
    ProgressAdapter mAdapter = new ProgressAdapter(activity, responses);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(mAdapter);
    new RecyclerViewAnimation().rerunLayoutAnimation(recyclerView);
  }

  @Override
  public void showErrorLayout() {
    groupError.setVisibility(View.VISIBLE);
  }

  @Override
  public void removeErrorLayout() {
    groupError.setVisibility(GONE);
  }

  @Override
  public void showNoResultLayout() {
    groupEmpty.setVisibility(View.VISIBLE);
    tv_empty_msg.setText(R.string.msg_empty_history_all);
  }

  @Override
  public void removeNoResultLayout() {
    groupEmpty.setVisibility(GONE);
  }

  @Override
  public void logout() {
    activity.logout();
  }

  @OnClick(R.id.btn_refresh)
  public void onClick() {
    presenter.getHistoryList();
  }
}


