package io.reciteapp.recite.data.model;

import java.util.ArrayList;

public class PackageResponse {

  private boolean showPromotionalDialog;
  private ArrayList<PromotionalMessageResponse> messagePromotion;
  private ArrayList<PackageListResponse> packageList;

  public boolean isShowPromotionalDialog() {
    return showPromotionalDialog;
  }

  public void setShowPromotionalDialog(boolean showPromotionalDialog) {
    this.showPromotionalDialog = showPromotionalDialog;
  }

  public ArrayList<PromotionalMessageResponse> getMessageResponse() {
    return messagePromotion;
  }

  public void setMessageResponse(
      ArrayList<PromotionalMessageResponse> messagePromotion) {
    this.messagePromotion = messagePromotion;
  }

  public ArrayList<PackageListResponse> getPackageListResponses() {
    return packageList;
  }

  public void setPackageListResponses(
      ArrayList<PackageListResponse> packageList) {
    this.packageList = packageList;
  }
}
