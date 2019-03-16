package io.reciteapp.recite.submissionlist.user;


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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.ProgressBarAnimation;
import io.reciteapp.recite.customview.animation.RecyclerViewAnimation;
import io.reciteapp.recite.data.model.SubmissionListResponse;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.di.component.SubmissionListFragmentComponent;
import io.reciteapp.recite.di.module.SubmissionListModule;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.submissionlist.user.SubmissionListContract.Presenter;
import io.reciteapp.recite.utils.NotificationUtils;
import io.reciteapp.recite.utils.Utils;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * PLEASE READ.
 * THIS IS SUBMISSION LIST FOR USER
 * A simple {@link Fragment} subclass.
 */
public class SubmissionListFragment extends Fragment implements SubmissionListContract.View,
    OnQueryTextListener {

  @Inject
  Presenter presenter;

  @Inject
  @Named("service")
  NetworkCallWrapper service;

  @BindView(R.id.search_view)
  SearchView searchView;
  @BindView(R.id.group_empty)
  Group groupEmpty;
  @BindView(R.id.group_error)
  Group groupError;
  @BindView(R.id.btn_refresh)
  Button btnErrorRefresh;
  @BindView(R.id.loadingView)
  AppCompatImageView imgLoading;
  @BindView(R.id.submissionRecyclerView)
  RecyclerView recyclerView;
  @BindView(R.id.group_no_result)
  Group groupNoResult;
  Unbinder unbinder;
  @BindView(R.id.main_layout)
  ConstraintLayout mainLayout;

  private MainActivity activity;
  private SubmissionListFragmentComponent component;
  private ProgressBarAnimation progressBarAnimation;

  public SubmissionListFragment() {
    // Required empty public constructor
  }

  /**
   * Attach context convert to activity
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
    View rootView = inflater.inflate(R.layout.fragment_submission_list, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    getSubmissionListComponent().inject(this);
    init();
    return rootView;
  }

  /**
   * Remove searchview text when back to fragment
   */
  @Override
  public void onResume() {
    super.onResume();

    EditText searchViewEt = searchView.findViewById(R.id.search_src_text);
    searchViewEt.setText("");
  }

  /**
   * Unsubscribe and unbind view
   */
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    presenter.unSubscribe();
    unbinder.unbind();
  }

  /**
   * get submission list component from application component to initialized presenter, service and shared prefs
   * @return SubmissionListComponent
   */
  private SubmissionListFragmentComponent getSubmissionListComponent() {
    if (component == null) {
      component = App.getApp().getApplicationComponent()
          .newSubmissionListFragmentComponent(new SubmissionListModule());
    }
    return component;
  }

  /**
   * Initialized and setup view
   */
  private void init() {
    presenter.setView(this, service);
    setupView();
    presenter.getSubmissionList();
    new NotificationUtils(getActivity()).clearNotification(Constants.OPEN_FRAGMENT_SUBMISSION_ID);
  }

  /**
   * Setup view when first load this fragment
   */
  private void setupView() {
    mainLayout.requestLayout();

    progressBarAnimation = new ProgressBarAnimation();

    removeErrorLayout();
    removeNoResultLayout();
    removeWait();

    recyclerView.setVisibility(View.GONE);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
    recyclerView.setLayoutManager(linearLayoutManager);

    searchView.clearFocus();
    searchView.setIconifiedByDefault(false);
    searchView.setOnQueryTextListener(this);
    EditText searchViewEt = searchView.findViewById(R.id.search_src_text);
    searchViewEt.setText("");
    searchView.setQuery("", false);
    ImageView btn_close = searchView.findViewById(R.id.search_close_btn);
    btn_close.setOnClickListener(v -> {
      searchViewEt.setText("");
      mainLayout.requestLayout();
      new Utils().hideKeyboard(activity);
      presenter.getSubmissionList();
    });
  }

  @Override
  public void showWait() {
    progressBarAnimation.startAnimation(imgLoading);
    imgLoading.setVisibility(View.VISIBLE);
  }

  @Override
  public void removeWait() {
    progressBarAnimation.stopAnimation();
    imgLoading.setVisibility(View.GONE);
  }

  /**
   * Set submission list to recyclerView
   * @param responses result of submission list get from the server
   */
  @Override
  public void setSubmissionListToRv(List<SubmissionListResponse> responses) {
    recyclerView.setVisibility(View.VISIBLE);
    SubmissionListAdapter mAdapter = new SubmissionListAdapter(activity, responses);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(mAdapter);
    new RecyclerViewAnimation().rerunLayoutAnimation(recyclerView);
  }

  /**
   * Remove recycler view to avoid view overlap
   */
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

  /**
   * When there is no result when searching
   */
  @Override
  public void showNoResultLayout() {
    groupNoResult.setVisibility(View.VISIBLE);
  }

  @Override
  public void removeNoResultLayout() {
    groupNoResult.setVisibility(View.GONE);
  }

  @Override
  public void logout() {
    activity.logout();
  }

  /**
   * Refresh list
   */
  @OnClick(R.id.btn_refresh)
  public void onClick() {
    presenter.getSubmissionList();
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    presenter.filteredList(query);
    return false;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    return false;
  }
}
