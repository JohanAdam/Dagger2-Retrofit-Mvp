package io.reciteapp.recite.data.model;

import java.util.List;

public class CsProfileResponse {

  private String name;
  private String affiliation;
  private List<String> certification;
  private String rating;
  private int assessment;
  private String photo;
  private double responseTime;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAffiliation() {
    return affiliation;
  }

  public void setAffiliation(String affiliation) {
    this.affiliation = affiliation;
  }

  public List<String> getCertification() {
    return certification;
  }

  public void setCertification(List<String> certification) {
    this.certification = certification;
  }

  public String getRating() {
    return rating;
  }

  public void setRating(String rating) {
    this.rating = rating;
  }

  public int getAssessment() {
    return assessment;
  }

  public void setAssessment(int assessment) {
    this.assessment = assessment;
  }

  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }

  public double getResponseTime() {
    return responseTime;
  }

  public void setResponseTime(double responseTime) {
    this.responseTime = responseTime;
  }
}
