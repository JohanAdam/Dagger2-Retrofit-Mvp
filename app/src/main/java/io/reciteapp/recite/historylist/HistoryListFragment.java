package io.reciteapp.recite.historylist;


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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.ProgressBarAnimation;
import io.reciteapp.recite.customview.animation.RecyclerViewAnimation;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.di.component.HistoryListFragmentComponent;
import io.reciteapp.recite.di.module.HistoryListModule;
import io.reciteapp.recite.historylist.HistoryListContract.Presenter;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.data.model.HistoryResponse;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.utils.NotificationUtils;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryListFragment extends Fragment implements HistoryListContract.View {

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
  @BindView(R.id.recyclerView)
  RecyclerView recyclerView;
  @BindView(R.id.main_layout)
  ConstraintLayout mainLayout;
  Unbinder unbinder;
  private MainActivity activity;
  private HistoryListFragmentComponent component;
  private ProgressBarAnimation progressBarAnimation;

  public HistoryListFragment() {
    // Required empty public constructor
  }

  /**
   * Attach context to activity
   */
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MainActivity) {
      this.activity = (MainActivity) context;
    }
  }

  /**
   * Create view setup
   */
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_history_list, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    //Inject service and presenter
    getHistoryListComponent().inject(this);
    init();
    return rootView;
  }

  /**
   * Unsubscribe and unbind view
   */
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    presenter.unSubscribe();
  }

  /**
   * Get history list component from application component to initialized presenter, service and shared prefs
   * @return HistoryListComponent
   */
  private HistoryListFragmentComponent getHistoryListComponent() {
    if (component == null) {
      component = App.getApp().getApplicationComponent()
          .newHistoryListFragmentComponent(new HistoryListModule());
    }
    return component;
  }

  /**
   * Initialized and setup view
   */
  private void init() {
    //Set view to presenter
    presenter.setView(this, service);

    //Setup first view
    setupView();

    //Get history list
    presenter.getHistoryList();

    new NotificationUtils(getActivity()).clearNotification(Constants.OPEN_FRAGMENT_HISTORY_ID);
  }

  /**
   * Setup view when first load this fragment
   */
  private void setupView() {

    Bundle bundle = this.getArguments();
    if (bundle != null) {
      String surahName = bundle.getString("surahName");
      String surahId = bundle.getString("surahId");
      presenter.setSurahName(surahName);
      presenter.setSurahId(surahId);
    }

    //Set progress bar animation
    progressBarAnimation = new ProgressBarAnimation();

    removeWait();
    removeNoResultLayout();
    removeErrorLayout();

    //initialized recyclerView
    recyclerView.setVisibility(View.GONE);
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
    loadingView.setVisibility(View.GONE);
  }

  /**
   * Set history list to recyclerview
   * @param responses result of history list get from server
   */
  @Override
  public void setHistoryListToRv(ArrayList<HistoryResponse> responses) {
    recyclerView.setVisibility(View.VISIBLE);
    HistoryListAdapter mAdapter = new HistoryListAdapter(activity, responses);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(mAdapter);
    new RecyclerViewAnimation().rerunLayoutAnimation(recyclerView);
  }

  /**
   * Error layout when there is error
   */
  @Override
  public void showErrorLayout() {
    groupError.setVisibility(View.VISIBLE);
  }

  @Override
  public void removeErrorLayout() {
    groupError.setVisibility(View.GONE);
  }

  /**
   * View when there is no result to show when searching
   */
  @Override
  public void showNoResultLayout() {
    groupEmpty.setVisibility(View.VISIBLE);
  }

  @Override
  public void removeNoResultLayout() {
    groupEmpty.setVisibility(View.GONE);
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
