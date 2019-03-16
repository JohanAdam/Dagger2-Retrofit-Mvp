package io.reciteapp.recite.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AudioV2 implements Parcelable, Serializable {

  private String id;
  private String containerName;
  private String resourceName;
  private String sasQueryString;
  private String audioUri;
  private String surahName;
  private String UpdatedAt;
  private long AudioDuration; //Send in mil
  private boolean IsCompleted;
  @SerializedName("ayatid_fk")
  private String ayatFK_ID;

  public AudioV2() {

  }

  protected AudioV2(Parcel in) {
    id = in.readString();
    containerName = in.readString();
    resourceName = in.readString();
    sasQueryString = in.readString();
    audioUri = in.readString();
    surahName = in.readString();
    UpdatedAt = in.readString();
    AudioDuration = in.readLong();
    IsCompleted = in.readByte() != 0;
    ayatFK_ID = in.readString();
  }

  public static final Creator<AudioV2> CREATOR = new Creator<AudioV2>() {
    @Override
    public AudioV2 createFromParcel(Parcel in) {
      return new AudioV2(in);
    }

    @Override
    public AudioV2[] newArray(int size) {
      return new AudioV2[size];
    }
  };

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getContainerName() {
    return containerName;
  }

  public void setContainerName(String containerName) {
    this.containerName = containerName;
  }

  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public String getSasQueryString() {
    return sasQueryString;
  }

  public void setSasQueryString(String sasQueryString) {
    this.sasQueryString = sasQueryString;
  }

  public String getAudioUri() {
    return audioUri;
  }

  public void setAudioUri(String audioUri) {
    this.audioUri = audioUri;
  }

  public String getSurahName() {
    return surahName;
  }

  public void setSurahName(String surahName) {
    this.surahName = surahName;
  }

  public String getUpdatedAt() {
    return UpdatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    UpdatedAt = updatedAt;
  }

  public long getAudioDuration() {
    return AudioDuration;
  }

  public void setAudioDuration(long audioDuration) {
    AudioDuration = audioDuration;
  }

  public boolean isCompleted() {
    return IsCompleted;
  }

  public void setCompleted(boolean completed) {
    IsCompleted = completed;
  }

  public String getAyatFK_ID() {
    return ayatFK_ID;
  }

  public void setAyatFK_ID(String ayatFK_ID) {
    this.ayatFK_ID = ayatFK_ID;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(id);
    dest.writeString(containerName);
    dest.writeString(resourceName);
    dest.writeString(sasQueryString);
    dest.writeString(audioUri);
    dest.writeString(surahName);
    dest.writeString(UpdatedAt);
    dest.writeLong(AudioDuration);
    dest.writeByte((byte) (IsCompleted ? 1 : 0));
    dest.writeString(ayatFK_ID);
  }
}
