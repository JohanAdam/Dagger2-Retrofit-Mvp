package io.reciteapp.recite.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PackageListResponse implements Parcelable {

  private String packageID;
  private int packageDuration;
  private int packagePrice;
  private String packageName;
  private String packageDescription;
  private String agencyID;
  private boolean isExpired;

  public static final Creator<PackageListResponse> CREATOR = new Creator<PackageListResponse>() {
    @Override
    public PackageListResponse createFromParcel(Parcel in) {
      return new PackageListResponse(in);
    }

    @Override
    public PackageListResponse[] newArray(int size) {
      return new PackageListResponse[size];
    }
  };

  private PackageListResponse(Parcel in) {
    packageID = in.readString();
    packageDuration = in.readInt();
    packagePrice = in.readInt();
    packageName = in.readString();
    packageDescription = in.readString();
    agencyID = in.readString();
    isExpired = in.readByte() != 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(packageID);
    dest.writeInt(packageDuration);
    dest.writeInt(packagePrice);
    dest.writeString(packageName);
    dest.writeString(packageDescription);
    dest.writeString(agencyID);
    dest.writeByte((byte) (isExpired ? 1 : 0));
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public String getPackageID() {
    return packageID;
  }

  public void setPackageID(String packageID) {
    this.packageID = packageID;
  }

  public int getPackageDuration() {
    return packageDuration;
  }

  public void setPackageDuration(int packageDuration) {
    this.packageDuration = packageDuration;
  }

  public int getPackagePrice() {
    return packagePrice;
  }

  public void setPackagePrice(int packagePrice) {
    this.packagePrice = packagePrice;
  }

  public String getPackageDescription() {
    return packageDescription;
  }

  public void setPackageDescription(String packageDescription) {
    this.packageDescription = packageDescription;
  }

  public String getAgencyID() {
    return agencyID;
  }

  public void setAgencyID(String agencyID) {
    this.agencyID = agencyID;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public boolean isExpired() {
    return isExpired;
  }

  public void setExpired(boolean expired) {
    isExpired = expired;
  }

  public static Creator<PackageListResponse> getCREATOR() {
    return CREATOR;
  }
}
