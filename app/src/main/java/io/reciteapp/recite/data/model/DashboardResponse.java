package io.reciteapp.recite.data.model;

public class DashboardResponse {

  //Total ReciteTime for user
  private int totalTime;
  //Remaining ReciteTime for user
  private int remainingTime;
  //Show last time user recite
  private int lastRecite;
  //Show total submission by user
  private int totalSubmission;
  //Mark for if user cs or not
  private boolean isCredible;
  //Show ads dialog
  private boolean isShowAdvert;
  //Show dialog email if email is not valid
  private boolean isValidEmail;
  //Recital throttle, disable surah list if true.
  private boolean isDisableRecital;
  //Contain ads image
  private String adImage;
  //Contain ads url when click image
  private String adUrl;
  //Contain ads message
  private String adMessage;

  public int getTotalTime() {
    return totalTime;
  }

  public void setTotalTime(int totalTime) {
    this.totalTime = totalTime;
  }

  public int getRemainingTime() {
    return remainingTime;
  }

  public void setRemainingTime(int remainingTime) {
    this.remainingTime = remainingTime;
  }

  public int getLastRecite() {
    return lastRecite;
  }

  public void setLastRecite(int lastRecite) {
    this.lastRecite = lastRecite;
  }

  public int getTotalSubmission() {
    return totalSubmission;
  }

  public void setTotalSubmission(int totalSubmission) {
    this.totalSubmission = totalSubmission;
  }

  public boolean isCredible() {
    return isCredible;
  }

  public void setCredible(boolean credible) {
    isCredible = credible;
  }

  public boolean isShowAdvert() {
    return isShowAdvert;
  }

  public void setShowAdvert(boolean showAdvert) {
    isShowAdvert = showAdvert;
  }

  public boolean isValidEmail() {
    return isValidEmail;
  }

  public void setValidEmail(boolean validEmail) {
    isValidEmail = validEmail;
  }

  public boolean isDisableRecital() {
    return isDisableRecital;
  }

  public void setDisableRecital(boolean disableRecital) {
    isDisableRecital = disableRecital;
  }

  public String getAdImage() {
    return adImage;
  }

  public void setAdImage(String adImage) {
    this.adImage = adImage;
  }

  public String getAdUrl() {
    return adUrl;
  }

  public void setAdUrl(String adUrl) {
    this.adUrl = adUrl;
  }

  public String getAdMessage() {
    return adMessage;
  }

  public void setAdMessage(String adMessage) {
    this.adMessage = adMessage;
  }
}
