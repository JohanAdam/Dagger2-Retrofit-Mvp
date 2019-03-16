package io.reciteapp.recite.data.model;

public class PromotionalMessageResponse {

  private String title;
  private String message;
  private String language;

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
}
