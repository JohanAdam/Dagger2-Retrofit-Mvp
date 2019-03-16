package io.reciteapp.recite.main;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.Drawer.OnDrawerItemClickListener;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.FontCache;
import io.reciteapp.recite.customview.dialog.DialogAds;
import io.reciteapp.recite.customview.dialog.DialogCoupon;
import io.reciteapp.recite.customview.dialog.DialogError;
import io.reciteapp.recite.customview.dialog.DialogInfo;
import io.reciteapp.recite.customview.dialog.DialogInfoOptional;
import io.reciteapp.recite.customview.dialog.DialogLoading;
import io.reciteapp.recite.customview.dialog.DialogReferral;
import io.reciteapp.recite.customview.dialog.DialogShare;
import io.reciteapp.recite.customview.dialog.DialogUpdate;
import io.reciteapp.recite.dashboard.DashboardFragment;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.di.component.MainActivityComponent;
import io.reciteapp.recite.di.module.MainActivityModule;
import io.reciteapp.recite.historylist.HistoryListFragment;
import io.reciteapp.recite.login.LoginActivity;
import io.reciteapp.recite.main.MainContract.Presenter;
import io.reciteapp.recite.main.MainContract.View;
import io.reciteapp.recite.preferences.PreferencesActivity;
import io.reciteapp.recite.progress.ProgressFragment;
import io.reciteapp.recite.quickstart.cs.QuickStartCsctivity;
import io.reciteapp.recite.quickstart.user.QuickStartActivity;
import io.reciteapp.recite.reload.ReloadFragment;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.root.BaseActivity;
import io.reciteapp.recite.share.ShareFragment;
import io.reciteapp.recite.submissionlist.cs.SubmissionListCsFragment;
import io.reciteapp.recite.submissionlist.user.SubmissionListFragment;
import io.reciteapp.recite.utils.Utils;
import io.reciteapp.recite.utils.Utils.SnackBarCallback;
import io.reciteapp.recite.verseid.VerseIdFragment;
import java.util.ArrayList;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements View, OnDrawerItemClickListener {

  @Inject
  @Named("service")
  NetworkCallWrapper service;

  @Inject
  Presenter presenter;

  @BindView(R.id.toolbar_title)
  TextView toolbarTitle;
  @BindView(R.id.toolbar_title_t)
  TextView toolbarTitleT;
  @BindView(R.id.include)
  Toolbar toolbar;
  @BindView(R.id.bottom_navigation)
  BottomNavigationView bottomNavigation;
  @BindView(R.id.coordinator_main)
  CoordinatorLayout coordinatorMain;

  private MainActivityComponent component;
  private Fragment fragment;
  private Drawer drawer;
  private DialogLoading loadingDialog = null;
  private DialogReferral dialogReferral = null;
  private DialogInfo dialogInfo = null;

  //This will hold surahlist as long the activity survive
//  public ArrayList<SurahListResponse> surahListMain = null;

  private Utils utils;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //render first view
    renderView();
    //Get component
    getMainActivityComponent().inject(this);
    init(savedInstanceState);
  }

  @Override
  protected void onResume() {
    Timber.d("onResume");
    super.onResume();
    //update log item properly to check if item change in onResume
    updateLogItem();
    presenter.checkLogin();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    //un subscribe to all network call
    presenter.unSubscribe();
  }

  @Override
  protected int getLayoutResourceId() {
    return R.layout.main_activity;
  }


  private void renderView() {
    ButterKnife.bind(this);

    utils = new Utils();

    //setup toolbar
    setSupportActionBar(toolbar);
    Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    Typeface typeface = FontCache.get("fonts/Raleway-ExtraBold.ttf", this);
    toolbarTitle.setTypeface(typeface);
    toolbarTitleT.setTypeface(typeface);

    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      Window window = this.getWindow();
      window.addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(ContextCompat.getColor(this, R.color.greyish_brown));
    }
  }

  private MainActivityComponent getMainActivityComponent() {
    if (component == null) {
      component = App.getApp().getApplicationComponent()
          .newDashboardFragmentComponent(new MainActivityModule());
    }
    return component;
  }

  private void init(Bundle savedInstanceState) {
    //set this view to presenter
    presenter.setView(this, service);

    //Initialized Drawer
    new DrawerBuilder().withActivity(this).build();
    drawer = setupDrawer()
        .withActivity(this)
        .withToolbar(toolbar)
        .withOnDrawerNavigationListener(clickedView -> {
          onBackPressed();
          return true;
        })
        .withSavedInstance(savedInstanceState)
        .build();

    //Set default fragment
    checkBundle();

    presenter.checkFirstUser();

    //Set bottom navigation click
    bottomNavigation.setOnNavigationItemSelectedListener(item -> {
      switch (item.getItemId()) {
        case R.id.action_home:
          fragment = new DashboardFragment();
          switchFragmentWithoutBackstack(fragment, Constants.TAG_MAIN_FRAGMENT);
          break;
        case R.id.action_verseid:
          fragment = new VerseIdFragment();
          switchFragmentWithoutBackstack(fragment, Constants.TAG_MAIN_FRAGMENT);
          break;
        case R.id.action_progress:
          fragment = new ProgressFragment();
          switchFragmentWithoutBackstack(fragment, Constants.TAG_MAIN_FRAGMENT);
          break;
      }
      return true;
    });
  }

  private void checkBundle() {
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      //Send by any fragment if being forced logout
      boolean isError = bundle.getBoolean("error");
      String errorText = bundle.getString("error_text");

      //Send by notification to check which fragment to show first
      String fragment = bundle.getString("fragment");
      String surahName = bundle.getString("surah_name");
      String ayat_id = bundle.getString("ayat_id");

      presenter.checkBundle(isError, errorText, fragment, surahName, ayat_id);
    } else {
      presenter.checkBundle(false, null, null, null, null);
    }
  }

  //START DRAWER
  public DrawerBuilder setupDrawer() {
    ArrayList<PrimaryDrawerItem> drawerItemArrayList = setupDrawerItem();

    return new DrawerBuilder()
        .addDrawerItems(
            drawerItemArrayList.get(0),
            drawerItemArrayList.get(1),
            drawerItemArrayList.get(2),
            drawerItemArrayList.get(3),
            drawerItemArrayList.get(4),
            drawerItemArrayList.get(5),
            drawerItemArrayList.get(6),
            drawerItemArrayList.get(7),
            drawerItemArrayList.get(8))
        .withSelectedItem(-1)
        .withOnDrawerItemClickListener(this)
        .addStickyDrawerItems(drawerItemArrayList.get(9));
  }

  /**
   * Setup drawer item
   * @return list of PrimaryDrawerItem
   */
  @Override
  public ArrayList<PrimaryDrawerItem> setupDrawerItem() {
    ArrayList<PrimaryDrawerItem> itemList = new ArrayList<>();

    PrimaryDrawerItem item_empty = new PrimaryDrawerItem()
        .withIdentifier(0)
        .withSelectable(false)
        .withEnabled(false);
    itemList.add(item_empty);

    PrimaryDrawerItem item_referral = new PrimaryDrawerItem()
        .withIdentifier(1)
        .withIcon(R.drawable.ic_reffral)
        .withName(R.string.title_referral)
        .withTag(R.string.title_referral)
        .withDisabledTextColor(ContextCompat.getColor(this, R.color.disable_item_text))
        .withSelectable(false)
        .withEnabled(false);
    itemList.add(item_referral);

    PrimaryDrawerItem item_redemption = new PrimaryDrawerItem()
        .withIdentifier(2)
        .withIcon(R.drawable.ic_redemption)
        .withName(R.string.title_redemption)
        .withTag(R.string.title_redemption)
        .withDisabledTextColor(ContextCompat.getColor(this, R.color.disable_item_text))
        .withSelectable(false)
        .withEnabled(false);
    itemList.add(item_redemption);

    PrimaryDrawerItem item_reload = new PrimaryDrawerItem()
        .withIdentifier(3)
        .withIcon(R.drawable.ic_reload)
        .withName(R.string.title_reload)
        .withTag(R.string.title_reload)
        .withDisabledTextColor(ContextCompat.getColor(this, R.color.disable_item_text))
        .withSelectable(false)
        .withEnabled(false);
    itemList.add(item_reload);

    PrimaryDrawerItem item_reviewer_test = new PrimaryDrawerItem()
        .withIdentifier(4)
        .withIcon(R.drawable.ic_reviewer_test)
        .withName(R.string.title_reviewer_test)
        .withTag(R.string.title_reviewer_test)
        .withDisabledTextColor(ContextCompat.getColor(this, R.color.disable_item_text))
        .withSelectable(false)
        .withEnabled(false);
    itemList.add(item_reviewer_test);

    PrimaryDrawerItem item_settings = new PrimaryDrawerItem()
        .withIdentifier(5)
        .withIcon(R.drawable.ic_settings)
        .withName(R.string.title_settings)
        .withTag(R.string.title_settings)
        .withDisabledTextColor(ContextCompat.getColor(this, R.color.disable_item_text))
        .withSelectable(false)
        .withEnabled(true);
    itemList.add(item_settings);

    PrimaryDrawerItem item_quick_start = new PrimaryDrawerItem()
        .withIdentifier(6)
        .withIcon(R.drawable.ic_quick_start)
        .withName(R.string.title_quick_start)
        .withTag(R.string.title_quick_start)
        .withDisabledTextColor(ContextCompat.getColor(this, R.color.disable_item_text))
        .withSelectable(false)
        .withEnabled(true);
    itemList.add(item_quick_start);

    PrimaryDrawerItem item_share = new PrimaryDrawerItem()
        .withIdentifier(9)
        .withIcon(R.drawable.ic_share)
        .withName(R.string.title_share)
        .withTag(R.string.title_share)
        .withDisabledTextColor(ContextCompat.getColor(this, R.color.disable_item_text))
        .withSelectable(false)
        .withEnabled(true);
    itemList.add(item_share);

    PrimaryDrawerItem item_about = new PrimaryDrawerItem()
        .withIdentifier(7)
        .withIcon(R.drawable.ic_about)
        .withName(R.string.title_about)
        .withTag(R.string.title_about)
        .withDisabledTextColor(ContextCompat.getColor(this, R.color.disable_item_text))
        .withSelectable(false)
        .withEnabled(false);
    itemList.add(item_about);

    PrimaryDrawerItem item_log = new PrimaryDrawerItem()
        .withIdentifier(8)
        .withIcon(R.drawable.ic_logout)
        .withName(R.string.title_login)
        .withTag(R.string.title_login)
        .withDisabledTextColor(ContextCompat.getColor(this, R.color.disable_item_text))
        .withSelectable(false);
    itemList.add(item_log);

    return itemList;
  }

  /**
   * Update drawer item by sending the position to presenter to search for the icon and name
   * @param position Position of the item
   * @param isEnable true for enable , false for disable
   */
  @Override
  public void updateDrawerItem(int position, boolean isEnable) {
    int icon = presenter.getDrawerItemIcon(position);
    int name = presenter.getDrawerItemTitle(position);

    PrimaryDrawerItem item = new PrimaryDrawerItem()
        .withIdentifier(position)
        .withIcon(icon)
        .withName(name)
        .withTag(name)
        .withDisabledTextColor(ContextCompat.getColor(this, R.color.disable_item_text))
        .withSelectable(false)
        .withEnabled(isEnable);
    drawer.updateItemAtPosition(item, position);
  }

  /**
   * Drawer item click function
   */
  @Override
  public boolean onItemClick(android.view.View view, int position, IDrawerItem drawerItem) {

    switch ((int) drawerItem.getIdentifier()) {
      case 0:
        //do nothing coz there is invisible item at the top
        break;
      case 1:
        showReferralDialog();
        break;
      case 2:
        showCouponDialog();
        break;
      case 3:
        openReloadActivity();
        break;
      case 4:
        openReviewerTestActivity();
        break;
      case 5:
        openSettingsActivity();
        break;
      case 6:
        presenter.openQuickActivity();
        break;
      case 7:
        openAboutActivity();
        break;
      case 8:
        presenter.logoutFunction();
        break;
      case 9:
        openShareFragment();
        break;
      default:
        break;
    }

    return false;
  }

  /**
   * Update login drawer item
   */
  @Override
  public void updateLogItem() {
    int name;
    name = presenter.getLogItemTitle();

    PrimaryDrawerItem item = new PrimaryDrawerItem()
        .withIdentifier(8)
        .withIcon(R.drawable.ic_logout)
        .withName(name)
        .withTag(name)
        .withDisabledTextColor(ContextCompat.getColor(this, R.color.disable_item_text))
        .withSelectable(false)
        .withEnabled(true);
    drawer.updateStickyFooterItem(item);
  }
//  END DRAWER

  /**
   * Show snackbar
   * @param messageResource message resources of string
   */
  @Override
  public void showSnackBar(int messageResource) {
    utils.getSnackBar(coordinatorMain, getString(messageResource)).show();
  }

  @Override
  public void showLoadingDialog() {
    if (loadingDialog == null) {
      loadingDialog = new DialogLoading();
      loadingDialog.showLoadingDialog(this, getString(R.string.msg_loading));
    }
  }

  @Override
  public void removeLoadingDialog() {
    if (loadingDialog != null) {
      if (loadingDialog.getDialog().isShowing()) {
        loadingDialog.removeLoadingDialog();
        loadingDialog = null;
      }
    }
  }

  /**
   * Show error properly via responseCode
   * @param responseCode error response Code
   * @param isKick true = kick from fragment ; false = normal error dialog
   */
  @Override
  public void showErrorDialog(int responseCode, boolean isKick) {
    DialogError dialogError = new DialogError();
    String message;
    if (responseCode == Constants.RESPONSE_CODE_INTERNALSERVERERROR) {
      message = getResources().getString(R.string.error_interal_server);
    } else if (responseCode == Constants.RESPONSE_CODE_EMAILNOTVALID) {
      message = getResources().getString(R.string.error_email_not_valid);
    } else if (responseCode == Constants.RESPONSE_CODE_UNAUTHORIZED) {
      message = getResources().getString(R.string.error_authentication);
    } else if (responseCode == Constants.RESPONSE_CODE_NO_INTERNET) {
      message = getResources().getString(R.string.error_no_connection);
    } else if (responseCode == Constants.RESPONSE_CODE_NOT_FOUND_CODE) {
      message = getResources().getString(R.string.error_not_found_code);
    } else if (responseCode == Constants.RESPONSE_CODE_CODE_USED) {
      message = getResources().getString(R.string.error_code_used);
    } else if (responseCode == Constants.RESPONSE_CODE_CODE_EXPIRED) {
      message = getResources().getString(R.string.error_code_expired);
    } else if (responseCode == Constants.RESPONSE_CODE_ACCOUNT_ALREADY_HAS_REFERRAL_CODE) {
      message = getResources().getString(R.string.error_code_account_already_has_referral_code);
    } else if (responseCode == Constants.RESPONSE_CODE_PROBLEM_CHECK_RECITE_TIME) {
      message = getResources().getString(R.string.error_check_recite_time);
    } else if (responseCode == Constants.RESPONSE_CODE_AUDIO_FILE_SILENCE) {
      message = getResources().getString(R.string.error_audio_silence);
    } else if (responseCode == Constants.RESPONSE_CODE_UNSUFFICIENT_CREDIT_AVAILABLE) {
      message = getResources().getString(R.string.error_recite_time_unsufficient);
    } else if (responseCode == Constants.RESPONSE_CODE_CREDIT_NOT_FOUND) {
      //Patch free credit history 400 > 4005
      message = getResources().getString(R.string.error_credit_not_available);
    } else if (responseCode == Constants.RESPONSE_CODE_LOCKED_NOT_FOUND) {
      //404 while upload audio attachment 404 > 4004
      //Either review to long, or already lock by other cs
      message = getResources().getString(R.string.error_audio_locked);
    } else {
      message = getResources().getString(R.string.error_default);
    }
    dialogError.showDialog(this, message, () -> {
      if (isKick) {
        onBackPressed();
      }
    });
  }

  @Override
  public void showReferralDialog() {
    dialogReferral = new DialogReferral();
    dialogReferral.showDialog(this, refCode -> {
      if (!TextUtils.isEmpty(refCode)) {
        presenter.processReferral(refCode);
      }
    });
  }

  @Override
  public void showInfoDialog(int title, int message) {
    dialogInfo = new DialogInfo();
    dialogInfo.showDialog(this, getString(title), getString(message),
        () -> {});
  }

  @Override
  public void showInfoDialog(String title, String message) {
    dialogInfo = new DialogInfo();
    dialogInfo.showDialog(this, title, message,
        () -> {});
  }

  @Override
  public void showShareDialog(String title, String desc) {
    //TODO logic suppose in presenter, Fix This
    if (!presenter.getCsStatus()) {
      DialogShare dialogShare = new DialogShare();
      dialogShare.showDialog(this, title, desc, () -> {
        fragment = new ShareFragment();
        switchFragmentAddBackstack(fragment, Constants.TAG_OTHERS_FRAGMENT);
      });
    }
  }


  @Override
  public void showUpdateDialog() {
    DialogUpdate dialogUpdate = new DialogUpdate();
    dialogUpdate.showDialog(this,
        getString(R.string.title_update_app),
        getString(R.string.msg_update_app), () -> {
          //bring user to playstore
          final String appPackageName = getPackageName(); // getPackageName()
          // from Context or Activity object
          try {
            startActivity(
                new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
          } catch (android.content.ActivityNotFoundException e) {
            e.printStackTrace();
            startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://ic_play.google.com/store/apps/details?id=" + appPackageName)));
          }
        });
  }

  @Override
  public void showEnrollDialog(String referralCode) {
    DialogInfoOptional dialogEnroll = new DialogInfoOptional();
    dialogEnroll.showDialog(this, getString(R.string.title_enroll), getString(R.string.msg_enroll),
        (int id_btnClick) -> {
          if (id_btnClick == R.id.btn_ok) {
            presenter.patchReferralCodeToUserProfile(referralCode);
          }
        });
  }

  @Override
  public void showCouponDialog() {
    DialogCoupon dialogCoupon = new DialogCoupon();
    dialogCoupon.showDialog(this, couponCode -> {
      if (!TextUtils.isEmpty(couponCode)) {
        //accept
        presenter.processCoupon(couponCode);
      }
    });
  }

  @Override
  public void showSponsorDialog(String message,
      String imageUrl,
      String redirectUrl,
      boolean disableCancel) {
    Intent intent = new Intent(this, DialogAds.class);
    intent.putExtra("message", message);
    intent.putExtra("imgUrl", imageUrl);
    intent.putExtra("redirectUrl", redirectUrl);
    intent.putExtra("disableCancel", disableCancel);
    startActivity(intent);
  }

  @Override
  public void openReloadActivity() {
    //TODO Reload page
    ReloadFragment reloadFragment = new ReloadFragment();
    switchFragmentAddBackstack(reloadFragment, Constants.TAG_OTHERS_FRAGMENT);
  }

  @Override
  public void openReviewerTestActivity() {
    //TODO open cs test page
  }

  @Override
  public void openSettingsActivity() {
    Intent intent = new Intent(this, PreferencesActivity.class);
    startActivity(intent);
  }

  @Override
  public void openQuickStartActivity() {
    Intent intent = new Intent(this, QuickStartActivity.class);
    startActivity(intent);
  }

  @Override
  public void openQuickStartCsActivity() {
    Intent intent = new Intent(this, QuickStartCsctivity.class);
    startActivity(intent);
  }


  @Override
  public void openAboutActivity() {
    //TODO open about page
  }

  @Override
  public void openLoginActivity() {
    Intent intent = new Intent(this, LoginActivity.class);
    startActivity(intent);
  }

  private void openShareFragment() {
    Fragment fragment = new ShareFragment();
    switchFragmentAddBackstack(fragment, Constants.TAG_OTHERS_FRAGMENT);
  }

  /**
   * Determined which fragment is default fragment
   * @param tag_fragment fragment to show
   */
  @Override
  public void openFirstViewFragment(String tag_fragment, String surahName, String ayatId) {
    if (tag_fragment.equals(Constants.TAG_MAIN_FRAGMENT)) {
      this.fragment = new DashboardFragment();
      switchFragmentWithoutBackstack(fragment, tag_fragment);
    } else {

      Fragment fragment;
      Bundle bundle_fragment;

      if (tag_fragment.equals(Constants.OPEN_FRAGMENT_SUBMISSION)) {
        //New submission
        if (presenter.getCsStatus()) {
          fragment = new SubmissionListCsFragment();
        } else {
          fragment = new SubmissionListFragment();
        }
      } else if (tag_fragment.equals(Constants.OPEN_FRAGMENT_HISTORY)) {
        //New rating, New Review
        fragment = new HistoryListFragment();
        bundle_fragment = new Bundle();
        bundle_fragment.putString("surahName", surahName);
        bundle_fragment.putString("ayatId", ayatId);
        fragment.setArguments(bundle_fragment);

      } else {
        //Reload
        fragment = new DashboardFragment();
      }

      switchFragmentAddBackstack(fragment, tag_fragment);
    }
  }

  //Show bottom navigation layout when needed
  public void showBottomLayout() {
    //Load animation
    if (bottomNavigation.getVisibility() != android.view.View.VISIBLE) {
      bottomNavigation.setVisibility(android.view.View.VISIBLE);
    }
  }

  //Hide bottom navigation layout when needed
  public void hideBottomLayout(){
    //Load animation
    bottomNavigation.setVisibility(android.view.View.GONE);
  }

  //Show snackbar with action
  public void showSnackBarPersistantWithAction(int messageResource, int buttonTitleResources, SnackBarCallback callback) {
    utils.getSnackBarWithAction(coordinatorMain, getString(messageResource), getString(buttonTitleResources), callback).show();
  }

  //Show fragment without back button access
  public void switchFragmentWithoutBackstack(Fragment fragment, String tag) {

    hideKeyboard();

    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.frame_layout_main, fragment, tag).commit();
  }

  //Show fragment with back button access
  public void switchFragmentAddBackstack(Fragment fragment, String tag) {

    hideKeyboard();

    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.frame_layout_main, fragment, tag);
    fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commit();
  }

  //Add fragment instead of switch
  public void addFragmentAddBackstack(Fragment fragment, String tag) {
    hideKeyboard();

    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.frame_layout_main, fragment, tag);
    fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commit();
  }

  //Hide keyboard
  public void hideKeyboard() {
    utils.hideKeyboard(this);
  }

  @Override
  public void onBackPressed() {
//    super.onBackPressed();
    if (drawer != null && drawer.isDrawerOpen()) {
      drawer.closeDrawer();
    } else {
      Fragment main_fragment = getSupportFragmentManager()
          .findFragmentByTag(Constants.TAG_MAIN_FRAGMENT);

      if (main_fragment != null && main_fragment.isVisible()) {
        finish();
      } else {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

          Fragment submissionDetailUserFragment = getSupportFragmentManager().findFragmentByTag(Constants.TAG_SUBMISSION_DETAIL_USER);
          if (submissionDetailUserFragment != null && submissionDetailUserFragment.isVisible()) {
            Timber.d("SubmissionDetailUser fragment detected.");

            if (utils.randomizerDialogPopup()) {
              showShareDialog(getResources().getString(R.string.title_get_recitetime), getResources().getString(R.string.msg_get_recite));
            }
          }

          getSupportFragmentManager().popBackStack();
        } else {
          super.onBackPressed();
        }
      }
    }
  }

  //Tell presenter to logout
  //Finish the activity and restart
  @Override
  public void logout() {
    new AuthenticationRequest().logout(this);

    presenter.logout();

    //refresh Activity
    finish();
    startActivity(getIntent());
  }

}
