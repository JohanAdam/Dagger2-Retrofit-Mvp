package io.reciteapp.recite.submissionlist.cs;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.ProgressBarAnimation;
import io.reciteapp.recite.customview.animation.RecyclerViewAnimation;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.di.component.SubmissionListFragmentCsComponent;
import io.reciteapp.recite.di.module.SubmissionListCsModule;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.data.model.SubmissionListResponse;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.submissionlist.cs.SubmissionListCsContract.Presenter;
import io.reciteapp.recite.utils.NotificationUtils;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

/**
 * This fragment Fetch Submission List for cs by sending 0 ; Newest, 1 ; Oldest , for sorting.
 */
public class SubmissionListCsFragment extends Fragment implements SubmissionListCsContract.View {

//TODO Check double get when open this page ,because of getTab and setTab afterwards

  @Inject
  Presenter presenter;

  @Inject
  @Named("service")
  NetworkCallWrapper service;

  MainActivity activity;
  SubmissionListFragmentCsComponent component;
  @BindView(R.id.group_empty)
  Group groupEmpty;
  @BindView(R.id.group_error)
  Group groupError;
  @BindView(R.id.btn_refresh)
  Button btnRefresh;
  @BindView(R.id.loadingView)
  AppCompatImageView imgLoading;
  @BindView(R.id.submissionRecyclerView)
  RecyclerView recyclerView;
  @BindView(R.id.tv_sort_title)
  TextView tvSortTitle;

  ProgressBarAnimation progressBarAnimation;
  Unbinder unbinder;

  public SubmissionListCsFragment() {
    // Required empty public constructor
  }

  /**
   * Get parent activity for context Get bottom navigation of parent activity and hide the bottom
   * navigation
   */
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MainActivity) {
      this.activity = (MainActivity) context;
      activity.hideBottomLayout();
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_submission_list_cs, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    setHasOptionsMenu(true);
    //Injection component
    getSubmissionListComponent().inject(this);
    init();
    return rootView;
  }

  /**
   * Unsubscribe all network call and unbind view
   */
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    presenter.unSubscribe();
    unbinder.unbind();
  }

  /**
   * Get bottom navigation in parent activity and show bottom navigation
   */
  @Override
  public void onDetach() {
    super.onDetach();
    activity.showBottomLayout();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_submission_list, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_sort_new:
        Timber.d("Clicked New");
        presenter.setSortPreferences(0);
        break;
      case R.id.menu_sort_oldest:
        Timber.d("Clicked oldest");
        presenter.setSortPreferences(1);
        break;
    }
    return true;
  }

  private SubmissionListFragmentCsComponent getSubmissionListComponent() {
    if (component == null) {
      component = App.getApp().getApplicationComponent()
          .newSubmissionListFragmentCsComponent(new SubmissionListCsModule());
    }
    return component;
  }

  private void init() {
    presenter.setView(this, service);
    setupView();
    new NotificationUtils(getActivity()).clearNotification(Constants.OPEN_FRAGMENT_SUBMISSION_ID);
  }

  /**
   * Setup first view for this fragment
   */
  private void setupView() {
    progressBarAnimation = new ProgressBarAnimation();

    removeEmptyLayout();
    removeErrorLayout();
    removeSubmissionList();
    removeWait();

    recyclerView.setVisibility(View.GONE);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
    recyclerView.setLayoutManager(linearLayoutManager);

    presenter.getSortPreferences();
  }

  /**
   * Set sort text
   * @param sortTextR get sort text resources
   */
  @Override
  public void setSortText(int sortTextR) {
    tvSortTitle.setText(getString(sortTextR));
  }

  /**
   * Show waiting view
   */
  @Override
  public void showWait() {
    progressBarAnimation.startAnimation(imgLoading);
    imgLoading.setVisibility(View.VISIBLE);
  }

  /**
   * Remove waiting view
   */
  @Override
  public void removeWait() {
    progressBarAnimation.stopAnimation();
    imgLoading.setVisibility(View.GONE);
  }

  /**
   * Set response get from server to recycler view
   *
   * @param responses SubmissionListResponse get from server
   */
  @Override
  public void setSubmissionListoRv(List<SubmissionListResponse> responses) {
    recyclerView.setVisibility(View.VISIBLE);
    SubmissionListCsAdapter mAdapter = new SubmissionListCsAdapter(activity, responses);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(mAdapter);
    new RecyclerViewAnimation().rerunLayoutAnimation(recyclerView);
    Timber.d("set list to rv %s", recyclerView.getVisibility());
  }

  @Override
  public void removeSubmissionList() {
    recyclerView.setVisibility(View.GONE);
  }

  @Override
  public void showErrorLayout() {
    groupError.setVisibility(View.VISIBLE);
  }

  @Override
  public void removeErrorLayout() {
    groupError.setVisibility(View.GONE);
  }

  @Override
  public void showEmptyLayout() {
    groupEmpty.setVisibility(View.VISIBLE);
  }

  @Override
  public void removeEmptyLayout() {
    groupEmpty.setVisibility(View.GONE);
  }

  /**
   * Logout process from parent activity
   */
  @Override
  public void logout() {
    activity.logout();
  }


  @OnClick(R.id.btn_refresh)
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btn_refresh:
        presenter.getSortPreferences();
        break;
    }
  }
}
