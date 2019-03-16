package io.reciteapp.recite.constants;

public class Constants {

  /*
  BASE URL
   */
  //Real Server Url (Malaysia)
//  public static final String BASE_URL = "https://reciteazureapp.azurewebsites.net";
  //    Server Test
//  public static String BASE_URL = "https://reciteweb.azurewebsites.net";

  public static String ContentTypeValue = "application/json";
  public static final String AZURE_ZUMO_VER = "ZUMO-API-VERSION";
  public static String AZURE_VER = "2.0.0";
  public static final String URL_SCHEME = "reciteazureapp";
  public static final String URL_BILLPLZ = "https://www.billplz.com/";

  /*
  REQUEST CODE
   */
  public static int LOGIN_GOOGLE_REQUEST_CODE = 0;
  public static int LOGIN_FACEBOOK_REQUEST_CODE = 1;

  /*
  RESPONSE CODE
   */
  public static int RESPONSE_CODE_SUCCESS = 200;
  public static int RESPONSE_CODE_FAILED = 2;
  public static int RESPONSE_CODE_NO_INTERNET = 1;
  public static final int RESPONSE_CODE_UNAUTHORIZED = 401;
  public static final int RESPONSE_CODE_EMAILNOTVALID = 400;
  public static final int RESPONSE_CODE_INTERNALSERVERERROR = 500;
  public static final int RESPONSE_CODE_NOT_FOUND = 404;
  public static final int RESPONSE_CODE_PROBLEM_CHECK_RECITE_TIME = 411;
  //Dialog ads code
  //400 Credit not found
  public static final int RESPONSE_CODE_CREDIT_NOT_FOUND = 4005;
  //Code response code
  public static final int RESPONSE_CODE_NOT_FOUND_CODE = 4040;
  public static final int RESPONSE_CODE_CODE_USED = 300;
  public static final int RESPONSE_CODE_CODE_EXPIRED = 5005;
  public static final int RESPONSE_CODE_ACCOUNT_ALREADY_HAS_REFERRAL_CODE = 4000;
  //Audio Upload response code
  public static final int RESPONSE_CODE_AUDIO_FILE_SILENCE = 912;
  public static final int RESPONSE_CODE_UNSUFFICIENT_CREDIT_AVAILABLE = 900;
  //Audio Attachment upload response code
  public static final int RESPONSE_CODE_LOCKED_NOT_FOUND = 4004;
  public static final int RESPONSE_CODE_BAD_REQUEST = 5005;

  //Fragment Tag
  /**
   * For all three tab in main activity
   */
  public static String TAG_MAIN_FRAGMENT = "main_fragment_tag";
  /**
   * Beside both above
   */
  public static String TAG_OTHERS_FRAGMENT = "others_tag";
  public static String TAG_SUBMISSION_DETAIL_USER = "detail_user_tag";
  /**
   * Notification bundle tag
   */
  public static int OPEN_DEFAULT_ID = 0;
  public static String OPEN_FRAGMENT_SUBMISSION = "openSubmissionList";
  public static int OPEN_FRAGMENT_SUBMISSION_ID = 1;
  public static String OPEN_FRAGMENT_HISTORY = "openFragmentHistoryList";
  public static int OPEN_FRAGMENT_HISTORY_ID = 2;

  //Shared Pref file
  public static String PREF_FILE = "PREF_FILE";

  //This app main url
  public static String BASE_URL = "Base_Url";
  public static String PREF_REFERRAL_CODE = "REFERRAL_CODE";
  public static String PREF_CS_STATE = "CS_STATE";
  public static String PREF_USER_NAME = "USER_NAME";
  public static String PREF_USER_ID = "USER_ID";
  public static String PREF_EMAIl = "USER_EMAIL";
  public static String PREF_TOKEN ="USER_TOKEN";
  //Google or Facebook
  public static String PREF_LOGIN_PROVIDER = "LOGIN_PROVIDER";
  //My or In
  public static String PREF_COUNTRY = "COUNTRY";
  //True for First Time Open the app
  public static String PREF_FIRST_USER = "FIRST_USER";
  //list of enrollment to show payment
  public static String PREF_ENROLL_LIST = "ENROLL_LIST";
  //Save Sort preferences in Submission List (Cs)
  public static String PREF_SORT_PREF = "TAB_DEFAULT";
  //Save message for notification *time*
  public static String PREF_MESSAGE = "MESSAGES";
  //Save referral time from Referral Programme
  public static String PREF_REFERRAL_TIME = "REF_TIME";

  //Country value
  public static String my = "my";
  public static String in = "in";

  /**
   * Bundle key
   */
  //SurahList > Recite
  public static String AYAT_SURAHNAME = "AyatParentName";
  public static String AYAT_SUBAYAT = "AyatSubAyat";
  public static String AYAT_ID = "AyatId";


  public static long defaultClickValue = 600;
  public static String share_url = "https://tinyurl.com/recite2";
  public static String STATS_ACTION_COPY = "copy";
  public static String STATS_ACTION_SHARE ="share";
}
