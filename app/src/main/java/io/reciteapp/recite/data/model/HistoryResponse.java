package io.reciteapp.recite.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Contain History List
 */

public class HistoryResponse implements Serializable {

  private String id;
  private String surahName;
  @SerializedName("ayatDetail") private String ayat;
  private boolean isReviewed;
  @SerializedName("submissionTime") private String submitted;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

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

  public String getSubmitted() {
    return submitted;
  }

  public void setSubmitted(String submitted) {
    this.submitted = submitted;
  }

  public boolean isReviewed() {
    return isReviewed;
  }

  public void setReviewed(boolean reviewed) {
    isReviewed = reviewed;
  }

}
