package io.reciteapp.recite.data.model;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class RegisteredUserProfileResponse {

  private String id;
  private String userID;
  private String firstname;
  private String lastname;
  private String nric;
  private String email;
  private String contactnumber;
  private String nationality;
  private String address;
  private String centerID;
  private int gender;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getNric() {
    return nric;
  }

  public void setNric(String nric) {
    this.nric = nric;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getContactnumber() {
    return contactnumber;
  }

  public void setContactnumber(String contactnumber) {
    this.contactnumber = contactnumber;
  }

  public String getNationality() {
    return nationality;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getCenterID() {
    return centerID;
  }

  public void setCenterID(String centerID) {
    this.centerID = centerID;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }
}
