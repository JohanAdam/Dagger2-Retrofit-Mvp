package io.reciteapp.recite.reload;


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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.ProgressBarAnimation;
import io.reciteapp.recite.customview.animation.RecyclerViewAnimation;
import io.reciteapp.recite.customview.dialog.DialogReload;
import io.reciteapp.recite.customview.dialog.DialogReload.dialogReloadCallback;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.di.component.ReloadFragmentComponent;
import io.reciteapp.recite.di.module.ReloadModule;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.data.model.PackageListResponse;
import io.reciteapp.recite.data.model.PackageResponse;
import io.reciteapp.recite.reload.ReloadContract.Presenter;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.utils.Utils;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

/**
 * Reload page
 */
public class ReloadFragment extends Fragment implements ReloadContract.View {

  @Inject
  Presenter presenter;

  @Inject
  @Named("service")
  NetworkCallWrapper service;

  @BindView(R.id.loadingBar)
  AppCompatImageView progressBar;
  @BindView(R.id.rc_reload)
  RecyclerView recyclerView;
  @BindView(R.id.group_empty)
  Group groupEmpty;
  @BindView(R.id.group_error)
  Group groupError;
  @BindView(R.id.btn_refresh)
  Button btnRefresh;

  DialogReload dialogReload = null;
  Unbinder unbinder;

  private MainActivity activity;
  private ReloadFragmentComponent component;
  private ProgressBarAnimation progressBarAnimation;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MainActivity) {
      this.activity = (MainActivity) context;
    }
  }

  public ReloadFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_reload, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    getReloadComponent().inject(this);
    init();
    return rootView;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    Timber.d("onDestroyView");
    presenter.unSubscribe();
    unbinder.unbind();
  }

  /**
   * Get component from application component to initialized presenter, service and shared
   * preferences
   *
   * @return component get from ApplicationComponent
   */
  private ReloadFragmentComponent getReloadComponent() {
    if (component == null) {
      component = App.getApp().getApplicationComponent()
          .newReloadComponent(new ReloadModule());
    }
    return component;
  }

  private void init() {
    presenter.setView(this, service);
    setupView();
  }

  private void setupView() {
    progressBar.setVisibility(View.GONE);
    progressBarAnimation = new ProgressBarAnimation();

    groupEmpty.setVisibility(View.GONE);
    groupError.setVisibility(View.GONE);

    recyclerView.setVisibility(View.GONE);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
    recyclerView.setLayoutManager(linearLayoutManager);

    presenter.getPackageList();
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

  @Override
  public void showLoadingDialog() {
    activity.showLoadingDialog();
  }

  @Override
  public void removeLoadingDialog() {
    activity.removeLoadingDialog();
  }

  @Override
  public void setReloadPackageToList(PackageResponse responses) {
    recyclerView.setVisibility(View.VISIBLE);
    ReloadAdapter mAdapter = new ReloadAdapter(this, responses.getPackageListResponses());
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(mAdapter);
    new RecyclerViewAnimation().rerunLayoutAnimation(recyclerView);
  }

  @Override
  public void payment(PackageListResponse data) {
    presenter.payment(data);
  }

  @Override
  public void showWebReload(PackageListResponse data) {
    Timber.d("showWebReload");
    new Utils().getToast(activity, "Kopi Pasta Pasta").show();
//    Intent intent = new Intent(activity, WebActivity.class);
//    Bundle b = new Bundle();
//    b.putString("da_id", data.getPackageID());
//    intent.putExtras(b);
//    startActivity(intent);
  }

  @Override
  public void showDialogReload(PackageListResponse data) {
    Timber.d("showDialogReload");
    dialogReload = new DialogReload();
    dialogReload.showDialog(activity, data, new dialogReloadCallback() {
      @Override
      public void onClick(String phoneNumber, String provider, String packageID) {
        presenter.getPayment(phoneNumber, provider, packageID);
      }
    });
  }

  @Override
  public void removeDialogReload() {
    if (dialogReload != null) {
      dialogReload.removeDialog();
    }
  }

  @Override
  public void showInfoDialog(String title, String desc) {
    activity.showInfoDialog(title, desc);
  }

  @Override
  public void showSuccessReloadDialog(String numberPhone) {
    String successText = getString(R.string.msg_success_reload, numberPhone);
    activity.showInfoDialog(getString(R.string.title_reload), successText);
  }

  @Override
  public void showErrorDialog(int responseCode, boolean isKick) {
    activity.showErrorDialog(responseCode, isKick);
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
  public void showNoPackageLayout() {
    groupEmpty.setVisibility(View.VISIBLE);
  }

  @Override
  public void removeNoPackageLayout() {
    groupEmpty.setVisibility(View.GONE);
  }

  @Override
  public void logout() {
    activity.logout();
  }

  @OnClick(R.id.btn_refresh)
  public void onClick() {
    presenter.getPackageList();
  }
}
