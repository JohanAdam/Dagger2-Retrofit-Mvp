package io.reciteapp.recite.login;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.dialog.DialogAccLinking;
import io.reciteapp.recite.customview.dialog.DialogAccLinking.DialogAccLinkingCallback;
import io.reciteapp.recite.customview.dialog.DialogError;
import io.reciteapp.recite.customview.dialog.DialogError.DialogErrorCallback;
import io.reciteapp.recite.customview.dialog.DialogLoading;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.di.component.LoginActivityComponent;
import io.reciteapp.recite.di.module.LoginModule;
import io.reciteapp.recite.login.LoginContract.Presenter;
import io.reciteapp.recite.login.LoginContract.View;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.root.BaseActivity;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

public class LoginActivity extends BaseActivity implements View {

  @Inject
  Presenter presenter;

  @Inject
  @Named("service")
  NetworkCallWrapper service;

  LoginActivityComponent component;
  DialogLoading loading;
  DialogError dialogError;
  DialogError dialogConnection;
  DialogAccLinking dialogLinking = null;
  @BindView(R.id.title_rec)
  TextView titleRec;
  @BindView(R.id.title_ite)
  TextView titleIte;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);

    renderView();
    getLoginComponent().inject(this);
    init();

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    presenter.unSubscribe();
  }

  @Override
  protected int getLayoutResourceId() {
    return R.layout.activity_login;
  }

  private void renderView() {

    Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Raleway-ExtraBold.ttf");
    titleRec.setTypeface(typeface);
    titleIte.setTypeface(typeface);

  }

  private LoginActivityComponent getLoginComponent() {
    if (component == null) {
      component = App.getApp().getApplicationComponent()
          .newLoginActivityComponent(new LoginModule());
    }
    return component;
  }

  private void init() {
    presenter.setView(this, service);
  }

  @Override
  public void showWait() {
    Timber.d("showWait");
    if (loading == null) {
      loading = new DialogLoading();
      loading.showLoadingDialog(this, getResources().getString(R.string.msg_loading));
    }
  }

  @Override
  public void removeWait() {
    Timber.d("removeWait");
    if (loading != null) {
      if (loading.getDialog().isShowing()) {
        loading.removeLoadingDialog();
        loading = null;
      }
    }
  }

  @Override
  public void onSuccess() {
    Timber.d("successView");
    finish();
  }

  @Override
  public void onFailure(int responseCode) {
    Timber.e("failedView responseCode is %s", responseCode);
    if (dialogError == null) {
      dialogError = new DialogError();
      dialogError.showDialog(this, getResources().getString(R.string.error_login),
          new DialogErrorCallback() {
            @Override
            public void onClick() {
              dialogError = null;
            }
          });
    }
  }

  @Override
  public void showDialogLinking(String uid, String token, String provider,
      DialogAccLinkingCallback callback) {
    if (dialogLinking == null) {
      dialogLinking = new DialogAccLinking();
      dialogLinking.showDialog(this, provider, callback);
    }
  }

  @Override
  public void showNoInternetConnectionDialog() {
    if (dialogConnection == null) {
      dialogConnection = new DialogError();
      dialogConnection.showDialog(this, getResources().getString(R.string.error_no_connection),
          new DialogErrorCallback() {
            @Override
            public void onClick() {
              dialogConnection = null;
            }
          });
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Timber.d("onActivityResult ");
    Timber.d("resultCode %s", resultCode);
    if (resultCode == RESULT_OK) {

      // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
      if (requestCode == Constants.LOGIN_GOOGLE_REQUEST_CODE) {
        Timber.d("GOOGLE LOGIN REQUEST");
        presenter.processGoogleLogin(GoogleSignIn.getSignedInAccountFromIntent(data));
      } else {
        Timber.d("FACEBOOK LOGIN REQUEST");
        CallbackManager callbackManager = presenter.getCallbackManager();
        if (callbackManager != null) {
          //Pass the activity result to Facebook Sdk(AuthenticationRequest)
          callbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
          //callback manager is null, so we need to initialized back
          presenter.processFacebookLogin();
        }
      }

    } else {
      if (resultCode == RESULT_CANCELED) {
        removeWait();
      } else {
        removeWait();
        //Logout
        presenter.logout();
        onFailure(resultCode);
      }
    }
  }

  /**
   * InitializedGoogleClient in view because it need context
   */
  @Override
  public void initializedGoogleClient() {
    AuthenticationRequest loginRequest = new AuthenticationRequest();
    loginRequest.initializedGoogleSignIn(this);
    loginRequest.callGoogleSignIn(this);
  }

//  /**
//   * Google login button function
//   *
//   * @param uid User id
//   * @param token Token authentication
//   */
//  @Override
//  public void googleLogin(String uid, String token) {
//    if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
//      presenter.loginWithExistingData(uid, token);
//    } else {
//      presenter.getGoogleLogin();
//    }
//  }

//  /**
//   * Facebook login button function
//   *
//   * @param uid User id
//   * @param token Token authentication
//   */
//  @Override
//  public void facebookLogin(String uid, String token) {
//    if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
//      presenter.loginWithExistingData(uid, token);
//    } else {
//      presenter.processFacebookLogin();
//      LoginManager.getInstance().logInWithReadPermissions(this,
//          Arrays.asList("email", "public_profile"));
//    }
//  }

  @Override
  public void facebookLoginPermission() {
    LoginManager.getInstance().logInWithReadPermissions(this,
        Arrays.asList("email", "public_profile"));
  }

  @OnClick({R.id.btn_sign_in_google, R.id.btn_sign_in_facebook})
  public void onClick(android.view.View view) {

//    String uid = presenter.getUserId();
//    String token = presenter.getToken();

    switch (view.getId()) {
      case R.id.btn_sign_in_google:
        Timber.d("Pressed Btn Google");
//        googleLogin(uid, token);
        presenter.getGoogleLogin();
        break;
      case R.id.btn_sign_in_facebook:
        Timber.d("Pressed Btn Facebook");
//        facebookLogin(uid, token);
        presenter.getFacebookLogin();
        break;
    }
  }

  @Override
  public void logout() {
    new AuthenticationRequest().logout(this);

    presenter.logout();

    //refresh Activity
    finish();
    startActivity(getIntent());
  }
}
