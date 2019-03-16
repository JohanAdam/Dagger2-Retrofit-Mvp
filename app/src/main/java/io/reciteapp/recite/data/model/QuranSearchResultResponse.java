package io.reciteapp.recite.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class QuranSearchResultResponse implements Parcelable {

  private String verseName;
  private String ayah;

  public QuranSearchResultResponse(String verseName, String ayah) {
    this.verseName = verseName;
    this.ayah = ayah;
  }

  public String getVerseName() {
    return verseName;
  }

  public void setVerseName(String verseName) {
    this.verseName = verseName;
  }

  public String getAyah() {
    return ayah;
  }

  public void setAyah(String ayah) {
    this.ayah = ayah;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {

  }
}
