package io.reciteapp.recite.data.model;

/**
 * Created by Johan on 15/11/2016.
 */

public class CouponResponse {

  private int response;
  private String couponImage;
  private String couponMessage;
  private String couponUrl;

  public int getResponse() {
    return response;
  }

  public void setResponse(int response) {
    this.response = response;
  }

  public String getCouponImage() {
    return couponImage;
  }

  public void setCouponImage(String couponImage) {
    this.couponImage = couponImage;
  }

  public String getCouponMessage() {
    return couponMessage;
  }

  public void setCouponMessage(String couponMessage) {
    this.couponMessage = couponMessage;
  }

  public String getCouponUrl() {
    return couponUrl;
  }

  public void setCouponUrl(String couponUrl) {
    this.couponUrl = couponUrl;
  }
}
