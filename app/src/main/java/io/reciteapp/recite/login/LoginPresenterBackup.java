//package io.reciteapp.recite.login;
//
//import com.microsoft.windowsazure.mobileservices.MobileServiceActivityResult;
//import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
//import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
//import io.reciteapp.recite.constants.Constants;
//import io.reciteapp.recite.data.AzureServiceAdapter;
//import io.reciteapp.recite.data.networking.NetworkCallWrapper;
//import io.reciteapp.recite.data.networking.call.UserProfileCall.GetUserProfileResponseCallback;
//import io.reciteapp.recite.login.LoginContract.View;
//import io.reciteapp.recite.data.model.UserProfileResponse;
//import java.util.ArrayList;
//import java.util.HashMap;
//import rx.Subscription;
//import rx.subscriptions.CompositeSubscription;
//import timber.log.Timber;
//
//public class LoginPresenterBackup implements LoginContract.Presenter {
//
//  private static String TAG = "LoginPresenterBackup";
//
//  private LoginContract.Model model;
//  private LoginContract.View view;
//  private NetworkCallWrapper service;
//  private CompositeSubscription subscriptions;
//
//  public LoginPresenterBackup(LoginContract.Model model) {
//    this.subscriptions = new CompositeSubscription();
//    this.model = model;
//  }
//
//  @Override
//  public void setView(View view, NetworkCallWrapper service) {
//    this.view = view;
//    this.service = service;
//  }
//
//  /**
//   * Check for view after a network call to avoid view null when result return from call
//   * @return true for view is attach, false for view is null
//   */
//  private boolean isViewAttached() {
//    return view != null;
//  }
//
//  @Override
//  public String getToken() {
//    return model.getToken();
//  }
//
//  @Override
//  public String getUserId() {
//    return model.getUserId();
//  }
//
//  @Override
//  public void getGoogleLogin() {
//    Timber.d( "%s getGoogleLogin", TAG);
//    //If empty new login
//    Timber.d("no uid and token, login new");
//    HashMap<String, String> h = new HashMap<>();
//    h.put("access_type", "offline");
//    model.getGoogleLogin(h);
//
//  }
//
//  @Override
//  public void processGoogleLogin() {
//    Timber.d("%s processGoogleLogin ", TAG);
//    Timber.d("result.LoggedIn is %s", result.isLoggedIn());
//    if (result.isLoggedIn()) {
//      //Logged in
//
//      model.setUIDAndToken(mClient.getCurrentUser().getUserId(),
//          mClient.getCurrentUser().getAuthenticationToken(),
//          Constants.LOGIN_PROVIDER_GOOGLE);
//
//      //Access User profile table
//      getUserProfile();
//
//    } else {
//      //Not logged in , usually cause by user cancelled
//      if (isViewAttached()) {
//        view.removeWait();
//        logout();
//      }
//    }
//  }
//
//  @Override
//  public void processFacebookLogin(String uid, String token) {
//    model.processFacebookLogin();
//  }
//
//  @Override
//  public void processFacebookLogin(
//      MobileServiceActivityResult result,
//      MobileServiceClient mClient) {
//    if (result.isLoggedIn()) {
//
//      model.setUIDAndToken(mClient.getCurrentUser().getUserId(),
//          mClient.getCurrentUser().getAuthenticationToken(),
//          Constants.LOGIN_PROVIDER_FACEBOOK);
//
//      //Access User profile table
//      getUserProfile();
//
//    } else {
//      //Not logged in, usually cause by user cancelled
//     if (isViewAttached()) {
//       view.removeWait();
//       logout();
//     }
//    }
//  }
//
//  @Override
//  public void loginWithExistingData(String uid, String token) {
//    if (isViewAttached()) {
//      MobileServiceUser user = new MobileServiceUser(uid);
//      user.setAuthenticationToken(token);
//
//      AzureServiceAdapter.getInstance().getClient().setCurrentUser(user);
//      view.removeWait();
//      view.onSuccess();
//    }
//  }
//
//  @Override
//  public void unSubscribe() {
//    Timber.d("%s unSubscribe", TAG);
//    subscriptions.unsubscribe();
//    view = null;
//  }
//
//  private void getUserProfile() {
//    //Access User profile table
//    String token = model.getToken();
//    Subscription subscription = service.getUserProfile(token,
//        new GetUserProfileResponseCallback() {
//          @Override
//          public void onSuccess(ArrayList<UserProfileResponse> result) {
//            Timber.d("successView getUserProfile");
//            //Success
//            if (isViewAttached()) {
//              for (UserProfileResponse userProfileResponse : result) {
//                model.setUserProfile(userProfileResponse);
//              }
//              view.removeWait();
//              view.onSuccess();
//            }
//          }
//
//          @Override
//          public void onError(int responseCode) {
//            Timber.e("onError getUserProfile");
//            if (isViewAttached()) {
//              view.removeWait();
//              view.onFailure(responseCode);
//              logout();
//            }
//          }
//        });
//
//    subscriptions.add(subscription);
//  }
//
//  @Override
//  public void logout(){
//    AzureServiceAdapter.getInstance().logout();
//    model.logout();
//  }
//
//}
