package io.reciteapp.recite.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SurahListResponse implements Parcelable, ParentListItem {

  private String surahName;
  private String ayat;
  @SerializedName("ayatLists") 
  private List<AyatListsResponse> listayat;

  public SurahListResponse() {
  }

  protected SurahListResponse(Parcel in) {
    listayat = in.createTypedArrayList(AyatListsResponse.CREATOR);
    surahName = in.readString();
    ayat = in.readString();
  }

  public static final Creator<SurahListResponse> CREATOR = new Creator<SurahListResponse>() {
    @Override
    public SurahListResponse createFromParcel(Parcel in) {
      return new SurahListResponse(in);
    }

    @Override
    public SurahListResponse[] newArray(int size) {
      return new SurahListResponse[size];
    }
  };

  public String getSurahName() {
    return surahName;
  }

  public void setSurahName(String surahName) {
    this.surahName = surahName;
  }

  public String getAyat() {
    return ayat;
  }

  public void setAyat(String ayat) {
    this.ayat = ayat;
  }

  public void setListayat(List<AyatListsResponse> listayat) {
    this.listayat = listayat;
  }

  public List<AyatListsResponse> getListayat() {
    return listayat;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeTypedList(listayat);
    dest.writeString(surahName);
    dest.writeString(ayat);
  }

  @Override
  public List<AyatListsResponse> getChildItemList() {
    return listayat;
  }

  @Override
  public boolean isInitiallyExpanded() {
    return false;
  }
}
