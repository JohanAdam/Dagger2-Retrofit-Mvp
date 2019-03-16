package io.reciteapp.recite.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class UserProfileResponse {

  @SerializedName("id")
  @Expose
  private String id;

  @SerializedName("isCredible")
  @Expose
  private boolean isCredible;

  @SerializedName("email")
  @Expose
  private String email;

  @SerializedName("name")
  @Expose
  private String name;

  @SerializedName("referralCode")
  @Expose
  private String referralCode;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public boolean isCredible() {
    return isCredible;
  }

  public void setCredible(boolean credible) {
    isCredible = credible;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getReferralCode() {
    return referralCode;
  }

  public void setReferralCode(String referralCode) {
    this.referralCode = referralCode;
  }
}
