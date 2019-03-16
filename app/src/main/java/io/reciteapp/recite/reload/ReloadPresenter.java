package io.reciteapp.recite.reload;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.call.reload.PaymentCall.GetPaymentCallback;
import io.reciteapp.recite.data.networking.call.reload.ReloadCall.GetReloadCallback;
import io.reciteapp.recite.data.model.PackageListResponse;
import io.reciteapp.recite.data.model.PackageResponse;
import io.reciteapp.recite.data.model.PaymentResponse;
import io.reciteapp.recite.data.model.PromotionalMessageResponse;
import io.reciteapp.recite.reload.ReloadContract.Model;
import io.reciteapp.recite.reload.ReloadContract.Presenter;
import io.reciteapp.recite.reload.ReloadContract.View;
import java.util.ArrayList;
import java.util.Locale;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class ReloadPresenter implements Presenter {

  private NetworkCallWrapper service;
  private View view;
  private Model model;
  private CompositeSubscription subscriptions;

  public ReloadPresenter(Model model) {
    this.model = model;
  }

  @Override
  public void setView(View view, NetworkCallWrapper service) {
    this.view = view;
    this.service = service;
    this.subscriptions = new CompositeSubscription();
  }

  private boolean isViewAttached() {
    return view != null;
  }

  @Override
  public void getPackageList() {
    view.showWait();
    view.removeNoPackageLayout();
    view.removeErrorLayout();

    String token = model.getToken();
    Subscription subscription = service.getReloadPackage(token, new GetReloadCallback() {
      @Override
      public void onSuccess(PackageResponse packageResponses) {
        if (isViewAttached()) {
          view.removeWait();

          if (packageResponses.isShowPromotionalDialog()) {
            setupInfoDialog(packageResponses.getMessageResponse());
          }

          if (packageResponses.getPackageListResponses().size() >= 1) {
            view.setReloadPackageToList(packageResponses);
          } else {
            view.showNoPackageLayout();
          }
        }
      }

      @Override
      public void onError(NetworkError networkError) {
        if (isViewAttached()) {
          view.removeWait();
          view.showErrorLayout();
        }
      }
    });

    subscriptions.add(subscription);

  }

  @Override
  public void payment(PackageListResponse data) {
    if (model.getCountry().equals(Constants.my)) {
      //Malaysia
      view.showWebReload(data);
    } else {
      //Indonesia
      view.showDialogReload(data);
    }
  }

  private void setupInfoDialog(ArrayList<PromotionalMessageResponse> messageResponse) {
    String currentLanguage = Locale.getDefault().getLanguage();
    Timber.d("currentLanguage %s", currentLanguage);
    for (int i = 0; i < messageResponse.size(); i++) {
      if (messageResponse.get(i).getLanguage().toLowerCase().trim().equals(currentLanguage)) {
        view.showInfoDialog(messageResponse.get(i).getTitle(), messageResponse.get(i).getMessage());
      }
    }
  }

  @Override
  public void getPayment(String phone_number, String provider, String packageId) {
    view.showLoadingDialog();

    String token = model.getToken();
    Subscription subscription = service.getPayment(token,
        phone_number, provider, packageId,
        new GetPaymentCallback() {
          @Override
          public void onSuccess(PaymentResponse paymentResponse) {
            if (isViewAttached()) {
              view.removeLoadingDialog();
              view.removeDialogReload();

              if (paymentResponse.isResult()) {
                view.showSuccessReloadDialog(phone_number);
              } else {
                view.showInfoDialog("Error", paymentResponse.getError_message());
              }
            }
          }

          @Override
          public void onError(NetworkError networkError) {
            if (isViewAttached()) {
              view.removeLoadingDialog();
              view.showErrorDialog(networkError.getResponseCode(), false);
            }
          }
        });

    subscriptions.add(subscription);

  }

  @Override
  public void unSubscribe() {
    subscriptions.unsubscribe();
    view = null;
  }
}
