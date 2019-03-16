package io.reciteapp.recite.surahlist;


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
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.ProgressBarAnimation;
import io.reciteapp.recite.customview.animation.RecyclerViewAnimation;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.di.component.SurahListFragmentComponent;
import io.reciteapp.recite.di.module.SurahListModule;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.data.model.SurahListResponse;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.surahlist.SurahListContract.Presenter;
import io.reciteapp.recite.surahlist.SurahListRecyclerView.SurahListAdapter;
import io.reciteapp.recite.utils.Utils;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class SurahListFragment extends Fragment implements SurahListContract.View,
    OnQueryTextListener {

  @Inject
  Presenter presenter;

  @Inject
  @Named("service")
  NetworkCallWrapper service;

  @BindView(R.id.search_view)
  SearchView searchView;
  @BindView(R.id.group_error)
  Group groupError;
  @BindView(R.id.btn_refresh)
  Button btnRefresh;
  @BindView(R.id.img_loading)
  AppCompatImageView imgLoading;
  @BindView(R.id.recyclerView)
  RecyclerView recyclerView;
  @BindView(R.id.group_no_result)
  Group groupNoResult;
  @BindView(R.id.main_layout)
  ConstraintLayout mainLayout;
  Unbinder unbinder;
  //Parent activity
  private MainActivity activity;
  //Get surah list component
  private SurahListFragmentComponent component;
  private ProgressBarAnimation progressBarAnimation;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MainActivity) {
      this.activity = (MainActivity) context;
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_surah_list, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    //Inject service and presenter
    getSurahListComponent().inject(this);
    init();

    return rootView;
  }

  /**
   * Clear SearchView EditText previous search text if available on fragment resume.
   */
  @Override
  public void onResume() {
    super.onResume();

    //For clearing search view when return from recite after search
    EditText searchViewEt = searchView.findViewById(R.id.search_src_text);
    searchViewEt.setText("");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Timber.e("ONDESTROY");
    App.getApp().mustDie(this);
  }

  /**
   * UnSubscriber all network call from this fragment before detach.
   * Unbind all layout.
   */
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    Timber.d("onDestroyView");
    //cancel all call from this fragment
    presenter.unSubscribe();
    unbinder.unbind();
  }

  /**
   * Get surah list component from application component to initialized presenter,
   * service and shared preferences
   * @return component get from ApplicationComponent
   */
  private SurahListFragmentComponent getSurahListComponent() {
    if (component == null) {
      component = App.getApp().getApplicationComponent()
          .newSurahListFragmentComponent(new SurahListModule());
    }
    return component;
  }

  /**
   * Initialized this SurahListFragment
   */
  private void init() {
    presenter.setView(this, service);

    //setup animation
    setupView();

    //Get surah list
    presenter.getSurahList();
  }

  /**
   * Setup layout when startup
   */
  private void setupView() {
    //Give main layout focus to avoid searchview get the focus
    mainLayout.requestLayout();

    //Set progress bar animation
    progressBarAnimation = new ProgressBarAnimation();

    removeErrorLayout();
    removeNoResultLayout();
    removeWait();

    //initialized recyclerView
    recyclerView.setVisibility(View.GONE);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
    recyclerView.setLayoutManager(linearLayoutManager);

    //setup search view
    searchView.clearFocus();
    searchView.setIconifiedByDefault(false);
    searchView.setOnQueryTextListener(this);
    EditText searchViewEt = searchView.findViewById(R.id.search_src_text);
    searchViewEt.setText("");
    searchView.setQuery("", false);
    ImageView closeButton = searchView.findViewById(R.id.search_close_btn);
    closeButton.setOnClickListener(v -> {
      Timber.d("onSearch X click");
      searchViewEt.setText("");
      mainLayout.requestLayout();
      new Utils().hideKeyboard(activity);
      presenter.getSurahList();
    });
  }

  /**
   * Show loading layout
   */
  @Override
  public void showWait() {
    progressBarAnimation.startAnimation(imgLoading);
    imgLoading.setVisibility(View.VISIBLE);
  }

  /**
   * Remove loading layout
   */
  @Override
  public void removeWait() {
    if (isAdded()) {
      progressBarAnimation.stopAnimation();
      imgLoading.setVisibility(View.GONE);
    }
  }

  /**
   * Set response to RecyclerView
   * @param responses response get from server to RecyclerView adapter
   */
  @Override
  public void setSurahList(ArrayList<SurahListResponse> responses) {
    //show recyclerview
    recyclerView.setVisibility(View.VISIBLE);
    //attach response to recyclerView adapter
    SurahListAdapter mAdapter = new SurahListAdapter(activity, responses);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(mAdapter);
    new RecyclerViewAnimation().rerunLayoutAnimation(recyclerView);
  }

  /**
   * Save latest SurahList get from server set SurahListMain in MainActivity
   * @param response save response get from server to MainActiviy
   */
  @Override
  public void setSurahListMain(ArrayList<SurahListResponse> response) {
//    activity.surahListMain = response;
  }

  /**
   * Get SurahListMain that cache in MainActivity
   * @return SurahListMain saved in MainActivity
   */
  @Override
  public ArrayList<SurahListResponse> getSurahListMain() {
//    return activity.surahListMain;
    return null;
  }

  /**
   * Remove SurahList RecyclerView
   */
  //hide recyclerview to avoid any mis click
  @Override
  public void removeSurahList() {
    recyclerView.setVisibility(View.GONE);
  }

  /**
   * Show Error layout
   */
  @Override
  public void showErrorLayout() {
    groupError.setVisibility(View.VISIBLE);
  }

  /**
   * Remove error layout
   */
  @Override
  public void removeErrorLayout() {
    groupError.setVisibility(View.GONE);
  }

  /**
   * Show No Result layout
   */
  @Override
  public void showNoResultLayout() {
    groupNoResult.setVisibility(View.VISIBLE);
  }

  /**
   * Remove No Result layout
   */
  @Override
  public void removeNoResultLayout() {
    groupNoResult.setVisibility(View.GONE);
  }

  /**
   * Get logout process from Main Activity
   */
  @Override
  public void logout() {
    activity.logout();
  }

  @OnClick({R.id.btn_refresh})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btn_refresh:
        presenter.getSurahList();
        break;
    }
  }

  /**
   *
   * @param query search text that user pressed enter
   * Get presenter to filtered the main list to search the key
   */
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
