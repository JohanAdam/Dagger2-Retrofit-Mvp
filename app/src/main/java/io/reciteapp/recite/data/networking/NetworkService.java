package io.reciteapp.recite.data.networking;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.model.AudioV2;
import io.reciteapp.recite.data.model.CouponResponse;
import io.reciteapp.recite.data.model.CsProfileResponse;
import io.reciteapp.recite.data.model.DashboardResponse;
import io.reciteapp.recite.data.model.HistoryDetailResponse;
import io.reciteapp.recite.data.model.HistoryResponse;
import io.reciteapp.recite.data.model.PackageResponse;
import io.reciteapp.recite.data.model.PaymentResponse;
import io.reciteapp.recite.data.model.QuranSearchResultResponse;
import io.reciteapp.recite.data.model.ReciteVersionResponse;
import io.reciteapp.recite.data.model.RegisteredUserProfileResponse;
import io.reciteapp.recite.data.model.SubmissionListResponse;
import io.reciteapp.recite.data.model.SubmissionRecorderResponse;
import io.reciteapp.recite.data.model.SurahListResponse;
import io.reciteapp.recite.data.model.UploadFileResponse;
import io.reciteapp.recite.data.model.UserProfileResponse;
import io.reciteapp.recite.data.model.UstazReviewResponse;
import java.util.ArrayList;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.Single;

public interface NetworkService {

  //TODO Remove Azure Zumo Ver , not needed

  // ---- MISC ----
  //Get Recite Latest Version
  @GET("/tables/ReciteVersion")
  Observable<ArrayList<ReciteVersionResponse>> getReciteVersion(
      @Header(Constants.AZURE_ZUMO_VER) String ver);

  //Get user profile
  @GET("/tables/Userprofile")
  Observable<ArrayList<UserProfileResponse>> getUserProfile(
      @Header("Content-Type") String contentType,
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token);

  //Get referral user info for share page
  @GET("api/ReferralCode")
  Single<String> getReferralCode(
      @Header("Authorization") String token,
      @Query("id") String id);

  //Get statistics report
  @GET("api/Stats")
  Single<Void> getStats(
      @Header("Authorization") String token,
      @Query("action") String actionStats);

  //TODO not exist in indonesia server
  @POST("/tables/RegisteredUserProfile")
  Single<RegisteredUserProfileResponse> postRegisteredUserProfile(
      @Header("Content-Type") String contentType,
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token,
      @Body RegisteredUserProfileResponse body);

  @POST("api/Coupon")
  Single<CouponResponse> postCouponCode(
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token,
      @Query("couponName") String coupon);

  //Send notification token to server
  @POST("api/NotificationRegister")
  Single<Void> postNotificationToken(
      @Header("Authorization") String token,
      @Query("registrationid") String tokenId);

  //Update email in server userProfile table
  @PATCH("api/Email")
  Single<String> patchEmail(
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token,
      @Query("email") String email);

  //Tell server that user redeem this(id) credit history
  @PATCH("/api/FreeCreditHistory")
  Single<Void> patchFreeCreditHistory(
      @Header("Content-Type") String contentType,
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token,
      @Query("freeHistoryId") String historyId,
      @Query("isAccept") int isAccept);

  //Update referral Code in server userProfile table
  @PATCH("api/ReferralCode")
  Single<String> patchReferralCode(
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token,
      @Query("code") String code);

  // ---- DASHBOARD ----
  //Get Dashboard data
  @GET("api/dashboard")
  Single<DashboardResponse> getDashboard(
      @Header("Authorization") String token);

  // ---- SURAH LIST ----
  //Get SurahList
  @GET("api/surah")
  Observable<ArrayList<SurahListResponse>> getSurahList(
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token);

  // ---- RECITE ----
  //Get ReciteTime and two latest history
  @GET("api/submissionrecorder/{ayatid}")
  Single<SubmissionRecorderResponse> getReciteTime(
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token,
      @Path(value = "ayatid") String id);

  //Get SAS Query for audio upload (USER)
  @GET("api/audio/signature/{resourcename}/{audioduration}")
  Single<UploadFileResponse> getSasQueryUser(
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token,
      @Path("resourcename") String resourceName,
      @Path("audioduration") int duration);

  //Post audio data
  @POST("/api/audio")
  Single<AudioV2> postAudioDataUser(
      @Header("Content-Type") String contentType,
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token,
      @Body AudioV2 audioData);

  // ---- HISTORY LIST ----
  //Get history list by ayat
  @GET("api/submissionhistory/ayat/{ayatid}")
  Observable<ArrayList<HistoryResponse>> getHistoryList(
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token,
      @Path(value = "ayatid") String id);

  //Get history list all ayat
  @GET("api/submissionhistory/")
  Observable<ArrayList<HistoryResponse>> getAllHistoryList(
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token);

  // ---- HISTORY DETAIL ----
  //Get History Detail
  @GET("api/submissionhistory/{id}")
  Single<HistoryDetailResponse> getHistoryDetail(
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token,
      @Path(value = "id") String id);

  //Patch rating and comment rating cs to server
  @PATCH("api/RatingUstaz")
  Single<String> patchRatingCs(
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token,
      @Query("reviewID") String id,
      @Query("comment") String comment,
      @Query("rating") String rating);


  // ---- CS PROFILE ----
  //Get Cs Profile
  @GET("api/CredibleSourceProfile")
  Single<CsProfileResponse> getCsProfile(
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token,
      @Query("Id") String id);

  // ---- SUBMISSION LIST ----
  //Get submission list
  /**
   * Get Submission List
   * order : order in ASC or DESC
   * take : take is how many to take in one request.
   */
  @GET("api/ReviewSubmission/order/{order}/take/{take}")
  Observable<ArrayList<SubmissionListResponse>> getSubmissionList(
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token,
      @Path(value = "order") String order,
      @Path(value = "take") int take);

  // ---- SUBMISSION DETAIL ----
  //Get Submission Detail (USER)
  @GET("api/reviewsubmission/{id}")
  Single<HistoryDetailResponse> getSubmissionDetailUser(
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token,
      @Path("id") String id);

  //Get Submission Detail (CS)
  @GET("api/reviewsubmission/{id}")
  Single<String> getSubmissionDetailCs(
      @Header("Content-Type") String contentType,
      @Header("Authorization") String token,
      @Path("id") String id);

  //Report submission
  @POST("api/ReportAudio")
  Single<Void> postReport(
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token,
      @Query("Id") String id,
      @Query("Comment") String comment);

  //Post rating for submission (USER)
  @POST("api/Rating")
  Single<String> postSubmissionRating(
      @Header(Constants.AZURE_ZUMO_VER) String ver,
      @Header("Authorization") String token,
      @Query("audioID") String id,
      @Query("rating") String rating);

  //Get SAS Query for audio upload (CS)
  @GET("api/ustazreview/signature/{resourcename}")
  Observable<UploadFileResponse> getSasQueryCs(
      @Header("Authorization") String token,
      @Path("resourcename") String resourceName);

  //Post audio data
  @POST("/api/ustazreview")
  Observable<UstazReviewResponse> postUstazReviewData(
      @Header("Content-Type") String contentType,
      @Header("Authorization") String token,
      @Body UstazReviewResponse ustazReviewResponse);

  // ---- RELOAD ----
  //Get package list for reload
  @GET("api/RecitePackage")
  Single<PackageResponse> getReloadPackage(
      @Header("Authorization") String token);

  //Get payment
  @GET("api/Payment")
  Single<PaymentResponse> getPayment(
      @Header("Authorization") String token,
      @Query("telcoid") String telcoid,
      @Query("phone") String number,
      @Query("packageid") String packageId);

  // ---- VERSE ID ----
  //send search ayat to verse id
  @GET("api/quransearch")
  Single<QuranSearchResultResponse> getVerseId(@Header("Authorization") String token,
      @Query("searchword") String searchAyat);

}
