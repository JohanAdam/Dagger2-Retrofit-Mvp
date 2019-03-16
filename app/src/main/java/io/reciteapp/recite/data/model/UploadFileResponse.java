package io.reciteapp.recite.data.model;

public class UploadFileResponse {

  private String resourceName;
  private String audioUri;
  private String sasQueryString;

  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public String getAudioUri() {
    return audioUri;
  }

  public void setAudioUri(String audioUri) {
    this.audioUri = audioUri;
  }

  public String getSasQueryString() {
    return sasQueryString;
  }

  public void setSasQueryString(String sasQueryString) {
    this.sasQueryString = sasQueryString;
  }
}
