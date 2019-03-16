package io.reciteapp.recite.data.networking;

import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

import com.google.gson.Gson;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import java.io.IOException;
import retrofit2.adapter.rxjava.HttpException;

public class NetworkError extends Throwable {
  public static final int DEFAULT_ERROR_MESSAGE = R.string.error_default;
  public static final int NETWORK_ERROR_MESSAGE = R.string.error_no_connection;
  private static final String ERROR_MESSAGE_HEADER = "Error-Message";
  private final Throwable error;

  public NetworkError(Throwable e) {
    super(e);
    e.printStackTrace();
    this.error = e;
  }

  public String getMessage() {
    return error.getMessage();
  }

  public boolean isAuthFailure() {
    return error instanceof HttpException &&
        ((HttpException) error).code() == HTTP_UNAUTHORIZED;
  }

  public boolean isResponseNull() {
    return error instanceof HttpException && ((HttpException) error).response() == null;
  }


  public int getResponseCode() {
    error.printStackTrace();
    if (error instanceof retrofit2.HttpException) {
      retrofit2.HttpException exception = (retrofit2.HttpException) error;
      return exception.code();
    }
    if (error instanceof IOException) {
      return Constants.RESPONSE_CODE_NO_INTERNET;
    }
    return Constants.RESPONSE_CODE_FAILED;
  }

  public int getAppErrorMessageResources() {
    if (this.error instanceof IOException) return NETWORK_ERROR_MESSAGE;
    return DEFAULT_ERROR_MESSAGE;
  }

  protected String getJsonStringFromResponse(final retrofit2.Response<?> response) {
    try {
      String jsonString = response.errorBody().string();
      Response errorResponse = new Gson().fromJson(jsonString, Response.class);
      return errorResponse.status;
    } catch (Exception e) {
      return null;
    }
  }

  public Throwable getError() {
    return error;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NetworkError that = (NetworkError) o;

    return error != null ? error.equals(that.error) : that.error == null;

  }

  @Override
  public int hashCode() {
    return error != null ? error.hashCode() : 0;
  }
}