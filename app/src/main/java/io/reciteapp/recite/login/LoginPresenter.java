package io.reciteapp.recite.login;

import android.text.TextUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookException;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.AuthenticationRequest;
import io.reciteapp.recite.data.AuthenticationRequest.FacebookLoginCallback;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.call.NotificationTokenCall.PostNotificationCallback;
import io.reciteapp.recite.login.LoginContract.Model;
import io.reciteapp.recite.login.LoginContract.Presenter;
import io.reciteapp.recite.login.LoginContract.View;
import io.reciteapp.recite.utils.NetworkConnectivityHelper;
import io.reciteapp.recite.utils.NetworkConnectivityHelper.NetworkConnectivityHelperCallback;
import java.util.Objects;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class LoginPresenter implements Presenter {

  private View view;
  private Model model;
  private NetworkCallWrapper service;
  private CompositeSubscription subscriptions;
  private FirebaseAuth mAuth;
  private CallbackManager callbackManager;
  private AuthCredential accCollisionAuth = null;

  public LoginPresenter(Model model) {
    this.subscriptions = new CompositeSubscription();
    this.model = model;
    mAuth = FirebaseAuth.getInstance();
  }

  /**
   * Check for view after a network call to avoid view null when result return from call
   * @return true for view is attach, false for view is null
   */
  private boolean isViewAttached() {
    return view != null;
  }

  @Override
  public void setView(View view, NetworkCallWrapper service) {
    this.view = view;
    this.service = service;
  }

  private void checkForConnectivity(NetworkConnectivityHelperCallback callback) {
    Timber.d("checkForConnectivity");
    new NetworkConnectivityHelper(callback).execute();
  }

  @Override
  public void getGoogleLogin() {
    Timber.d("getGoogleLogin");
    view.showWait();

    checkForConnectivity(new NetworkConnectivityHelperCallback() {
      @Override
      public void onResult(boolean result) {
        if (result) {
          if (!TextUtils.isEmpty(model.getUserId()) && !TextUtils.isEmpty(model.getToken())) {
            loginWithExistingData(model.getUserId(), model.getToken());
          } else {
            view.initializedGoogleClient();
          }
        } else {
          view.showNoInternetConnectionDialog();
          view.removeWait();
        }
      }
    });
  }

  @Override
  public void processGoogleLogin(Task<GoogleSignInAccount> signedInAccountFromIntent) {
    Timber.d("processGoogleLogin");
    try {
      GoogleSignInAccount account = signedInAccountFromIntent.getResult(ApiException.class);
      AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
      firebaseAuthWithCredential(credential);
    } catch (ApiException e) {
      // The ApiException status code indicates the detailed failure reason.
      // Please refer to the GoogleSignInStatusCodes class reference for more information.
      Timber.e("signInResult:failed code=%s", e.getStatusCode());
      view.removeWait();
      view.logout();
      view.onFailure(e.getStatusCode());
    }
  }

  @Override
  public void getFacebookLogin() {
    Timber.d("getFacebookLogin");
    view.showWait();

    checkForConnectivity(new NetworkConnectivityHelperCallback() {
      @Override
      public void onResult(boolean result) {
        if (result) {
          if (!TextUtils.isEmpty(model.getUserId()) && !TextUtils.isEmpty(model.getToken())) {
            loginWithExistingData(model.getUserId(), model.getToken());
          } else {
            processFacebookLogin();
            view.facebookLoginPermission();
          }
        } else {
          view.showNoInternetConnectionDialog();
          view.removeWait();
        }
      }
    });
  }

  @Override
  public void processFacebookLogin() {
    Timber.d("processFacebookLogin");

    //initialized facebook callbackManager
    AuthenticationRequest loginRequest = new AuthenticationRequest();
    callbackManager = loginRequest.initializedFacebookSignIn(new FacebookLoginCallback() {
      @Override
      public void onSuccess(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuthWithCredential(credential);
      }

      @Override
      public void onError(FacebookException error) {
        Timber.d("onError processFacebookLogin");
        error.printStackTrace();
        view.removeWait();
        view.logout();
        view.onFailure(new NetworkError(error).getResponseCode());
      }
    });
  }

  @Override
  public CallbackManager getCallbackManager() {
    return callbackManager;
  }

  private void firebaseAuthWithCredential(AuthCredential accCredential) {
    mAuth.signInWithCredential(accCredential)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(Task<AuthResult> task) {
            if (task.isSuccessful()) {
              // Sign in success, update UI with the signed-in user's information
              if (accCollisionAuth == null) {
                FirebaseUser user = mAuth.getCurrentUser();
                getUserInfoFromFirebase(user);
              } else {
                linkingAccount(accCollisionAuth);
              }

            } else {
              // If sign in fails, display a message to the user.
              Timber.e("!task.isSuccessful ");

              if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                Timber.e("FirebaseAuthUserCollisionException currently login %s", accCredential.getProvider());

                view.showDialogLinking(model.getUserId(), model.getToken(), accCredential.getProvider(),
                    btnClick -> {
                      switch (btnClick) {
                        case R.id.btn_sign_in_facebook:
                          Timber.d("Login facebook without linking");
                          accCollisionAuth = null;
//                          view.facebookLogin(model.getUserId(), model.getToken());
                          getFacebookLogin();
                          break;
                        case R.id.btn_sign_in_google:
                          Timber.d("Login google without linking");
                          accCollisionAuth = null;
//                          view.googleLogin(model.getUserId(), model.getToken());
                          getGoogleLogin();
                          break;
                        case R.id.btn_link_account:
                          Timber.d("Login opposite and linking");
                          accCollisionAuth = accCredential;
                          if (accCredential.getProvider().equals(GoogleAuthProvider.PROVIDER_ID)) {
                            //Collision with google , need to login with facebook
//                            view.facebookLogin(model.getUserId(), model.getToken());
                            getFacebookLogin();
                          } else {
                            //Collision with facebook , need to login with google
//                            view.googleLogin(model.getUserId(), model.getToken());
                            getGoogleLogin();
                          }
                          break;
                      }
                    });
              } else {
                Timber.e("else ");
                Objects.requireNonNull(task.getException()).printStackTrace();
                view.removeWait();
                view.logout();
                view.onFailure(new NetworkError(task.getException()).getResponseCode());
              }
            }
          }
        });
  }

  private void linkingAccount(AuthCredential credential) {
    Timber.d("linkingAccount");
    accCollisionAuth = null;
    Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credential)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(Task<AuthResult> task) {
            if (task.isSuccessful()) {
              Timber.d("Successful linking");
              FirebaseUser user = mAuth.getCurrentUser();
              getUserInfoFromFirebase(user);
            } else {
              Timber.e("!Successful linking");
              Objects.requireNonNull(task.getException()).printStackTrace();
              view.removeWait();
              view.logout();
              view.onFailure(new NetworkError(task.getException()).getResponseCode());
            }
          }
        });
  }

  private void getUserInfoFromFirebase(FirebaseUser user) {

    if (user != null) {
      user.getIdToken(true).addOnSuccessListener(getTokenResult -> {
        //google.com || facebook.com
        Timber.d("onSuccess getUserInfoFromFirebase signInProvider %s", getTokenResult.getSignInProvider());
        String token = getTokenResult.getToken();
        model.setUIDAndToken(user.getUid(), "Bearer " + token, getTokenResult.getSignInProvider());
        model.setEmail(user.getEmail());

        String refreshedNotificationToken = FirebaseInstanceId.getInstance().getToken();
        sendNotificationToken(refreshedNotificationToken);
      });
    } else {
      Timber.e("getUserInfoFromFirebase user = null");
      view.removeWait();
      view.logout();
      view.onFailure(Constants.RESPONSE_CODE_FAILED);
    }

  }

  private void sendNotificationToken(String refreshedNotificationToken) {
    Timber.d("sendNotificationToken");

    String token = model.getToken();
    Subscription subscription = service.postNotificationToken(token, refreshedNotificationToken,
        new PostNotificationCallback() {
          @Override
          public void onSuccess() {
            Timber.d("onSuccess sendNotificationToken");
            view.removeWait();
            view.onSuccess();
          }

          @Override
          public void onError(int response) {
            Timber.e("onFailed sendNotificationToken responseCode %s", response);
            view.removeWait();
            view.onSuccess();
          }
        });

    subscriptions.add(subscription);
  }

  @Override
  public void loginWithExistingData(String uid, String token) {
    view.showWait();
    if (isViewAttached()) {
      if (mAuth != null) {
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        getUserInfoFromFirebase(currentUser);
      }
    }
  }

  @Override
  public void unSubscribe() {
    subscriptions.unsubscribe();
    view = null;
  }

  @Override
  public void logout() {
    model.logout();
  }
}
