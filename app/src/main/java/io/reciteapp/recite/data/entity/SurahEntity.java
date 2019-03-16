package io.reciteapp.recite.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "surah_table")
public class SurahEntity {

  @PrimaryKey
  @NonNull
  private String surah_name;

  private String ayat;

  // Contain a String json of AyatListResponse
  private String ayatListJson;

  @NonNull
  public String getSurah_name() {
    return surah_name;
  }

  public void setSurah_name(@NonNull String surah_name) {
    this.surah_name = surah_name;
  }

  public String getAyat() {
    return ayat;
  }

  public void setAyat(String ayat) {
    this.ayat = ayat;
  }

  public String getAyatListJson() {
    return ayatListJson;
  }

  public void setAyatListJson(String ayatListJson) {
    this.ayatListJson = ayatListJson;
  }
}
