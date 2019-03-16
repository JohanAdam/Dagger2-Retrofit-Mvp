package io.reciteapp.recite.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AyatListsResponse implements Parcelable {

  private String ayatId;
  private String subAyat;
  private int totalSubmission;

  public AyatListsResponse() {
  }

  protected AyatListsResponse(Parcel in) {
    ayatId = in.readString();
    subAyat = in.readString();
    totalSubmission = in.readInt();
  }

  public static final Creator<AyatListsResponse> CREATOR = new Creator<AyatListsResponse>() {
    @Override
    public AyatListsResponse createFromParcel(Parcel in) {
      return new AyatListsResponse(in);
    }

    @Override
    public AyatListsResponse[] newArray(int size) {
      return new AyatListsResponse[size];
    }
  };

  public String getAyatId() {
    return ayatId;
  }

  public void setAyatId(String ayatId) {
    this.ayatId = ayatId;
  }

  public String getSubAyat() {
    return subAyat;
  }

  public void setSubAyat(String subAyat) {
    this.subAyat = subAyat;
  }

  public int getTotalSubmission() {
    return totalSubmission;
  }

  public void setTotalSubmission(int totalSubmission) {
    this.totalSubmission = totalSubmission;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(ayatId);
    dest.writeString(subAyat);
    dest.writeInt(totalSubmission);
  }
}
