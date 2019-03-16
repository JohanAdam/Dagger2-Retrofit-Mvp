package io.reciteapp.recite.data.model;

public class PaymentResponse {

  private boolean result;
  private String error_code;
  private String error_message;

  public boolean isResult() {
    return result;
  }

  public void setResult(boolean result) {
    this.result = result;
  }

  public String getError_code() {
    return error_code;
  }

  public void setError_code(String error_code) {
    this.error_code = error_code;
  }

  public String getError_message() {
    return error_message;
  }

  public void setError_message(String error_message) {
    this.error_message = error_message;
  }
}
