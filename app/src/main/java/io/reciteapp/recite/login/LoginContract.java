package io.reciteapp.recite.login;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import io.reciteapp.recite.customview.dialog.DialogAccLinking.DialogAccLinkingCallback;
import io.reciteapp.recite.data.model.UserProfileResponse;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;

public interface LoginContract{

  interface View {

    void showWait();

    void removeWait();

    void onSuccess();

    void onFailure(int responseCode);

    void showDialogLinking(String uid, String token, String provider,
        DialogAccLinkingCallback callback);

    void showNoInternetConnectionDialog();

    void initializedGoogleClient();

    void facebookLoginPermission();

    void logout();
    }

  interface Model {

    String getToken();

    String getUserId();

    void setUIDAndToken(String userId,
        String token,
        String loginProvider);

    void setUserProfile(UserProfileResponse userProfileResponse);

    void logout();

    void setEmail(String email);
  }

  interface Presenter {

    void setView(View view, NetworkCallWrapper service);

    void getGoogleLogin();

    void processGoogleLogin(
        Task<GoogleSignInAccount> signedInAccountFromIntent);

    void getFacebookLogin();

    void processFacebookLogin();

    CallbackManager getCallbackManager();

    void loginWithExistingData(String uid, String token);

    void unSubscribe();

    void logout();
  }

}
