package io.reciteapp.recite.data.model;

import java.io.Serializable;

public class SubmissionListResponse implements Serializable {

  private String id;
  private String surahName;
  private String ayat;
  private String submitted;
  private boolean isLock;
  private boolean isPriority;

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

  public boolean isLock() {
    return isLock;
  }

  public void setLock(boolean lock) {
    isLock = lock;
  }

  public boolean getIsPriority() {
    return isPriority;
  }

  public void setIsPriority(boolean isPriority) {
    this.isPriority = isPriority;
  }

}
