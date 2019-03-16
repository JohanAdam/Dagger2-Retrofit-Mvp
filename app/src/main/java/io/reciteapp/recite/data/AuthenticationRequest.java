package io.reciteapp.recite.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import io.reciteapp.recite.BuildConfig;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.root.App;
import timber.log.Timber;
/**
 * This class contain Google SignIn , Facebook SignIn, Logout both and firebase
 * Refresh token;
 */

public class AuthenticationRequest {

  private GoogleSignInClient mGoogleSignInClient;

  public void initializedGoogleSignIn(Context context){

    // Configure sign-in to request the user's ID, email address, and basic
    // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.WEB_CLIENT_ID)
        .requestEmail()
        .build();

    mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
  }

  public void callGoogleSignIn(Activity activity) {
    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
    activity.startActivityForResult(signInIntent, Constants.LOGIN_GOOGLE_REQUEST_CODE);
  }

  public CallbackManager initializedFacebookSignIn(FacebookLoginCallback callback) {

    CallbackManager callbackManager = CallbackManager.Factory.create();

    LoginManager.getInstance().registerCallback(callbackManager,
        new FacebookCallback<LoginResult>() {
          @Override
          public void onSuccess(LoginResult loginResult) {
            Timber.d("onSuccess facebook login callback");
            callback.onSuccess(loginResult.getAccessToken());
          }

          @Override
          public void onCancel() {
            Timber.d("onCancel facebook login callback");
          }

          @Override
          public void onError(FacebookException error) {
            Timber.e("onError facebook login callback");
            callback.onError(error);
          }
        });

    return callbackManager;
  }

  public interface FacebookLoginCallback {
    void onSuccess(AccessToken accessToken);
    void onError(FacebookException error);
  }

  public void logout(Activity activity) {
    Timber.d("Logout");
    //Firebase logout
    FirebaseAuth.getInstance().signOut();

    //Remove web cookies
    CookieSyncManager.createInstance(activity);
    CookieManager cookieManager = CookieManager.getInstance();
    cookieManager.removeAllCookie();

    Timber.d("Logout Google");
    if (mGoogleSignInClient == null) {
      initializedGoogleSignIn(activity);
    }
    mGoogleSignInClient.signOut()
        .addOnCompleteListener(activity, task -> Timber.d("Logging out onComplete"));

    Timber.d("Logout Facebook");
    if (AccessToken.getCurrentAccessToken() != null) {
      LoginManager.getInstance().logOut();
    }
  }


  public void getRefreshToken(RefreshTokenCallback callback) {
    Timber.e("getRefreshToken");
    try {
      FirebaseAuth mAuth;
      mAuth = FirebaseAuth.getInstance();
      FirebaseUser mUser = mAuth.getCurrentUser();
      assert mUser != null;
      mUser.getIdToken(true).addOnSuccessListener(
          getTokenResult -> {
            Timber.d("onSuccessListener getRefreshToken");
            String newToken = "Bearer " + getTokenResult.getToken();
            new SharedPreferencesManager(App.getApp()).saveString(Constants.PREF_TOKEN, newToken);
            callback.onSuccess(newToken);
          })
          .addOnFailureListener(e -> {
            Timber.e("onFailureListener getRefreshToken");
            e.printStackTrace();
            callback.onFailed(new NetworkError(e));
          });
    } catch (NullPointerException e) {
    Timber.e("NullPointer getRefreshToken");
      e.printStackTrace();
      callback.onFailed(new NetworkError(e));
    }
  }

  public interface RefreshTokenCallback {
    void onSuccess(String newToken);
    void onFailed(NetworkError networkError);
  }

}
