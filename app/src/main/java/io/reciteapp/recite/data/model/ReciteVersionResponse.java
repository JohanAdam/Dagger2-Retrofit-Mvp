package io.reciteapp.recite.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class ReciteVersionResponse {

  @SerializedName("id")
  @Expose
  private String id;

  @SerializedName("currentVersion")
  @Expose
  private String currentVersion;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCurrentVersion() {
    return currentVersion;
  }

  public void setCurrentVersion(String currentVersion) {
    this.currentVersion = currentVersion;
  }
}
