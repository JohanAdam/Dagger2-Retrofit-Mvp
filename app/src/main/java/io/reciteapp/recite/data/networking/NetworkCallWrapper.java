package io.reciteapp.recite.data.networking;

import io.reciteapp.recite.data.model.AudioV2;
import io.reciteapp.recite.data.model.RegisteredUserProfileResponse;
import io.reciteapp.recite.data.model.UstazReviewResponse;
import io.reciteapp.recite.data.networking.call.CouponCall;
import io.reciteapp.recite.data.networking.call.CouponCall.PostCouponCallback;
import io.reciteapp.recite.data.networking.call.NotificationTokenCall;
import io.reciteapp.recite.data.networking.call.NotificationTokenCall.PostNotificationCallback;
import io.reciteapp.recite.data.networking.call.ReciteVersionCall;
import io.reciteapp.recite.data.networking.call.ReciteVersionCall.GetReciteVersionCallback;
import io.reciteapp.recite.data.networking.call.ReferralCall;
import io.reciteapp.recite.data.networking.call.ReferralCall.PatchReferralCodeCallback;
import io.reciteapp.recite.data.networking.call.StatsActionCall;
import io.reciteapp.recite.data.networking.call.StatsActionCall.GetStatsActionCallback;
import io.reciteapp.recite.data.networking.call.UserProfileCall;
import io.reciteapp.recite.data.networking.call.UserProfileCall.GetUserProfileResponseCallback;
import io.reciteapp.recite.data.networking.call.csprofile.CsProfileCall;
import io.reciteapp.recite.data.networking.call.csprofile.CsProfileCall.GetCsProfileCallback;
import io.reciteapp.recite.data.networking.call.dashboard.DashboardCall;
import io.reciteapp.recite.data.networking.call.dashboard.DashboardCall.GetDashboardResponseCallback;
import io.reciteapp.recite.data.networking.call.dashboard.EmailCall;
import io.reciteapp.recite.data.networking.call.dashboard.EmailCall.PatchEmailCallback;
import io.reciteapp.recite.data.networking.call.dashboard.FreeCreditHistoryCall;
import io.reciteapp.recite.data.networking.call.dashboard.FreeCreditHistoryCall.PatchFreeCreditHistory;
import io.reciteapp.recite.data.networking.call.dashboard.RegisteredUserProfileCall;
import io.reciteapp.recite.data.networking.call.dashboard.RegisteredUserProfileCall.PostRegisteredUserCallback;
import io.reciteapp.recite.data.networking.call.history.historydetail.HistoryDetailCall;
import io.reciteapp.recite.data.networking.call.history.historydetail.HistoryDetailCall.GetHistoryDetailCallback;
import io.reciteapp.recite.data.networking.call.history.historydetail.RatingCsCall;
import io.reciteapp.recite.data.networking.call.history.historydetail.RatingCsCall.PatchRatingCsCallback;
import io.reciteapp.recite.data.networking.call.history.historylist.HistoryListCall;
import io.reciteapp.recite.data.networking.call.history.historylist.HistoryListCall.GetHistoryListCallback;
import io.reciteapp.recite.data.networking.call.recite.AudioUploadCall;
import io.reciteapp.recite.data.networking.call.recite.AudioUploadCall.UploadAudioUploadCallback;
import io.reciteapp.recite.data.networking.call.recite.ReciteTimeCall;
import io.reciteapp.recite.data.networking.call.recite.ReciteTimeCall.GetReciteTimeCallback;
import io.reciteapp.recite.data.networking.call.reload.PaymentCall;
import io.reciteapp.recite.data.networking.call.reload.PaymentCall.GetPaymentCallback;
import io.reciteapp.recite.data.networking.call.reload.ReloadCall;
import io.reciteapp.recite.data.networking.call.reload.ReloadCall.GetReloadCallback;
import io.reciteapp.recite.data.networking.call.share.ShareCodeCall;
import io.reciteapp.recite.data.networking.call.share.ShareCodeCall.GetShareCodeCallback;
import io.reciteapp.recite.data.networking.call.submission.submissiondetail.ReportAudioCall;
import io.reciteapp.recite.data.networking.call.submission.submissiondetail.ReportAudioCall.PostReportCallback;
import io.reciteapp.recite.data.networking.call.submission.submissiondetail.cs.CsReviewRepository;
import io.reciteapp.recite.data.networking.call.submission.submissiondetail.cs.CsReviewRepository.CsReviewRepoCallback;
import io.reciteapp.recite.data.networking.call.submission.submissiondetail.cs.SubmissionDetailCallCs;
import io.reciteapp.recite.data.networking.call.submission.submissiondetail.cs.SubmissionDetailCallCs.GetSubmissionDetailCsCallback;
import io.reciteapp.recite.data.networking.call.submission.submissiondetail.user.SubmissionDetailCallUser;
import io.reciteapp.recite.data.networking.call.submission.submissiondetail.user.SubmissionDetailCallUser.GetSubmissionDetailUserCallback;
import io.reciteapp.recite.data.networking.call.submission.submissiondetail.user.SubmissionDetailCallUser.PostAudioSubmissionRatingCallback;
import io.reciteapp.recite.data.networking.call.submission.submissionlist.SubmissionListCall;
import io.reciteapp.recite.data.networking.call.submission.submissionlist.SubmissionListCall.GetSubmissionListCallback;
import io.reciteapp.recite.data.networking.call.surahlist.SurahListCall;
import io.reciteapp.recite.data.networking.call.surahlist.SurahListCall.GetSurahListCallback;
import io.reciteapp.recite.data.networking.call.verseid.VerseIdCall;
import io.reciteapp.recite.data.networking.call.verseid.VerseIdCall.VerseIdCallback;
import rx.Subscription;

/**
 * This class that will executes our subscriber
 *
 * - Create retrofit by Network Module
 * - Choose service in NetworkCallWrapper.class
 * - executes in Presenter
 */

public class NetworkCallWrapper {

  private NetworkService networkService;


  public NetworkCallWrapper(NetworkService networkService) {
    this.networkService = networkService;
  }

  public Subscription getReciteVersion(final GetReciteVersionCallback callback) {
    return new ReciteVersionCall().get(networkService, callback);
  }

  public Subscription getDashboard(String token, final GetDashboardResponseCallback callback) {
    return new DashboardCall().get(token, networkService, callback);
  }

  public Subscription getUserProfile(String token, final GetUserProfileResponseCallback callback) {
    return new UserProfileCall().get(token, networkService, callback);
  }

  public Subscription getSurahList(String token, GetSurahListCallback callback) {
    return new SurahListCall().get(token, networkService, callback);
  }

  public Subscription getShareCode(String token, String id, GetShareCodeCallback callback) {
    return new ShareCodeCall().get(token, id, networkService, callback);
  }

  public Subscription getStatsAction(String token, String action, GetStatsActionCallback callback) {
    return new StatsActionCall().get(token, action, networkService, callback);
  }

  public Subscription postRegisteredUser(String token,
      RegisteredUserProfileResponse dataModel,
      PostRegisteredUserCallback callback) {
    return new RegisteredUserProfileCall().post(token, dataModel, networkService, callback);
  }

  public Subscription patchEmail(String token, String email, PatchEmailCallback callback) {
    return new EmailCall().patch(token, email, networkService, callback);
  }

  public Subscription patchFreeCreditHistory(String token, boolean isAccept, String freeCreditHistoryId, PatchFreeCreditHistory callback){
    return new FreeCreditHistoryCall().patch(token, isAccept, freeCreditHistoryId, networkService, callback);
  }

  public Subscription patchReferralCode(String token, String refCode , PatchReferralCodeCallback callback) {
    return new ReferralCall().patch(token, refCode, networkService, callback);
  }

  public Subscription postCouponCode(String token, String coupon, PostCouponCallback callback) {
    return new CouponCall().post(token, coupon, networkService, callback);
  }

  public Subscription getReciteTime(String token, String surahId, GetReciteTimeCallback callback) {
    return new ReciteTimeCall().get(token, surahId, networkService, callback);
  }

  //Post audio user
  public Subscription postAudioService(String token,
      AudioV2 audioV2,
      UploadAudioUploadCallback callback) {
    return new AudioUploadCall().uploadAudio(token, audioV2, networkService, callback);
  }

  //Post ustaz review
  public Subscription postCsReviewService(String token,
      UstazReviewResponse ustazReviewResponse,
      String surahName,
      CsReviewRepoCallback callback) {
      return new CsReviewRepository().uploadCsReview(token, ustazReviewResponse, surahName, networkService, callback);
  }

  public Subscription postReportAudio(String token, String id, String text, PostReportCallback callback) {
    return new ReportAudioCall().post(token, id, text, networkService, callback);
  }

  //if surahId is null get All history
  public Subscription getHistoryList(String token, String ayatId, GetHistoryListCallback callback) {
    if (ayatId != null) {
      return new HistoryListCall().get(token, ayatId, networkService, callback);
    } else {
      return new HistoryListCall().getAll(token, networkService, callback);
    }
  }

  public Subscription getHistoryDetails(String token, String audioId, GetHistoryDetailCallback callback) {
    return new HistoryDetailCall().get(token, audioId, networkService, callback);
  }

  public Subscription patchRatingCs(String token, String id, String feedback, String rating, PatchRatingCsCallback callback) {
    return new RatingCsCall().patch(token, id, feedback, rating, networkService, callback);
  }

  public Subscription getCsProfile(String token, String id, GetCsProfileCallback callback) {
    return new CsProfileCall().get(token, id, networkService, callback);
  }

  public Subscription getSubmissionList(String token, String orderList, int takeValue, GetSubmissionListCallback callback) {
    return new SubmissionListCall().get(token, orderList, takeValue, networkService, callback);
  }

  public Subscription getSubmissionDetailUser(String token, String audioId, GetSubmissionDetailUserCallback callback) {
    return new SubmissionDetailCallUser().get(token, audioId, networkService, callback);
  }

  public Subscription postSubmissionRatingAudio(String token, String audioId, String rating, PostAudioSubmissionRatingCallback callback) {
    return new SubmissionDetailCallUser().postRating(token, audioId, rating, networkService, callback);
  }

  public Subscription getSubmissionDetailCs(String token, String audioId, GetSubmissionDetailCsCallback callback) {
    return new SubmissionDetailCallCs().get(token, audioId, networkService, callback);
  }

  public Subscription getReloadPackage(String token, GetReloadCallback callback) {
    return new ReloadCall().get(token, networkService, callback);
  }

  public Subscription getPayment(String token, String numberPhone, String provider, String packageId, GetPaymentCallback callback) {
    return new PaymentCall().get(token, numberPhone, provider, packageId, networkService, callback);
  }

  public Subscription postNotificationToken(String token, String notificationToken, PostNotificationCallback callback) {
    return new NotificationTokenCall().post(token, notificationToken, networkService, callback);
  }

  public Subscription getVerseId(String token, String searchWord, VerseIdCallback callback) {
    return new VerseIdCall().get(token, searchWord, networkService, callback);
  }

}
