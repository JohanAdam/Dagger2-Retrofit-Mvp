package io.reciteapp.recite.data.repository;

import android.os.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.reciteapp.recite.data.database.AppDatabase;
import io.reciteapp.recite.data.entity.SurahEntity;
import io.reciteapp.recite.data.model.AyatListsResponse;
import io.reciteapp.recite.data.model.SurahListResponse;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.call.surahlist.SurahListCall.GetSurahListCallback;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import rx.Subscription;
import timber.log.Timber;

/**
 * A Repository manages query threads and allows you to use multiple backends.
 * In the most common example, the Repository implements the logic for deciding whether to
 * fetch data from a network or use results cached in a local database.
 */
public class SurahRepository implements GetSurahListCallback {

  private String token;
  private NetworkCallWrapper networkService;
  private Subscription subscription;
  private DatabaseServiceCallback callback;
  private boolean isSync = false;
  private AsyncTask<Void, Void, ArrayList<SurahListResponse>> getSurahListQuery;

  public SurahRepository() {

  }

  public SurahRepository(String token, NetworkCallWrapper service) {
    this.token = token;
    this.networkService = service;
  }

  //Check local db first before get list.
  public void getSurahList(DatabaseServiceCallback callback) {
    Timber.d("getSurahList checkLocalDb");
    this.callback = callback;

    getSurahListQuery = new GetSurahList(new GetSurahListAsyncCallback() {
      @Override
      public void onFinish(ArrayList<SurahListResponse> surahListLocal) {
        if (surahListLocal == null || surahListLocal.size() == 0){
          Timber.d("Surah local db size is 0, fetch new");
          getSurahListOnline();
        } else {
          Timber.d("Surah local db size is %s", surahListLocal.size());
          callback.success(surahListLocal);
        }
      }
    }).execute();
  }

  private void getSurahListOnline() {
    Timber.d("getSurahListOnline");
    isSync = true;
    subscription = networkService.getSurahList(token, this);
  }

  public void removeSubscription(){
    Timber.e("removeSubscription");
    AppDatabase.destroyInstance();

    if (getSurahListQuery != null) {
      getSurahListQuery.cancel(true);
    }

    if (isSync) {
      isSync = false;
      if (subscription != null) {
        subscription.unsubscribe();
      }
    }
  }

  @Override
  public void onSuccess(ArrayList<SurahListResponse> surahListResponses) {
    Timber.d("onSuccess getSurahListOnline size get is %s", surahListResponses.size());
    //Saves in local
    saveListInLocal(surahListResponses);

    isSync = false;
    //push to presenter
    callback.success(surahListResponses);
  }

  @Override
  public void onError(NetworkError networkError) {
    Timber.e("onError %s", networkError.getResponseCode());
    isSync = false;
    callback.failed(networkError);
  }

  private interface GetSurahListAsyncCallback {
    void onFinish(ArrayList<SurahListResponse> surahListResponses);
  }

  private static class GetSurahList extends AsyncTask<Void, Void, ArrayList<SurahListResponse>> {

    GetSurahListAsyncCallback callback;

    private GetSurahList(GetSurahListAsyncCallback callback) {
      this.callback = callback;
    }

    @Override
    protected ArrayList<SurahListResponse> doInBackground(Void... voids) {
      Timber.d("doInBackground GetSurahListLocal");

      List<SurahEntity> surahEntityList = AppDatabase.getAppDatabase().surahDao().getAllSurah();

      ArrayList<SurahListResponse> surahList = new ArrayList<>();
      for (int i = 0; i < surahEntityList.size(); i++) {
        SurahListResponse surah = new SurahListResponse();
        surah.setSurahName(surahEntityList.get(i).getSurah_name());
        surah.setAyat(surahEntityList.get(i).getAyat());

        Gson gson = new Gson();
        Type ayatListType = new TypeToken<List<AyatListsResponse>>(){}.getType();
        List<AyatListsResponse> ayatLists = gson.fromJson(surahEntityList.get(i).getAyatListJson(), ayatListType);
        surah.setListayat(ayatLists);
//        Timber.d("add %s", i);
        surahList.add(surah);
      }

      Timber.d("Returned size %s", surahList.size());
      return surahList;
    }

    @Override
    protected void onPostExecute(ArrayList<SurahListResponse> surahListResponses) {
      super.onPostExecute(surahListResponses);
      Timber.d("onPostExecute GetSurahListLocal");
      if (!isCancelled()) {
        callback.onFinish(surahListResponses);
      }
    }
  }

  private void saveListInLocal(List<SurahListResponse> surahListResponses) {
    Timber.d("saveListInLocal size save is %s", surahListResponses.size());
    //Check if database available for avoid conflict, start saving in complete delete
    new DeleteSurahList(surahListResponses).execute();
  }

  private static class DeleteSurahList extends AsyncTask<Void, Void, Void> {

    List<SurahListResponse> surahListResponses;

    DeleteSurahList(List<SurahListResponse> surahListResponses) {
      this.surahListResponses = surahListResponses;
    }

    @Override
    protected Void doInBackground(Void... voids) {
      Timber.d("doInBackground delete surah table");
      AppDatabase.getAppDatabase().surahDao().deleteAll();
      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      Timber.d("onPostExecute DeleteSurahList");

      //Start saving surah list
      new SaveSurahList(surahListResponses).execute();
    }
  }

  private static class SaveSurahList extends AsyncTask<Void, Void, Void> {

    List<SurahListResponse> surahListResponses;

    SaveSurahList(List<SurahListResponse> surahListResponses) {
      Timber.d("SaveSurahList Async");
      this.surahListResponses = surahListResponses;
    }

    @Override
    protected Void doInBackground(Void... voids) {
      Timber.d("doInBackground SaveSurahList size %s", surahListResponses.size());

      List<SurahEntity> surahEntityList = new ArrayList<>();
      for (int i = 0; i < surahListResponses.size(); i++) {
        SurahEntity surahEntity = new SurahEntity();
        surahEntity.setSurah_name(surahListResponses.get(i).getSurahName());
        surahEntity.setAyat(surahListResponses.get(i).getAyat());

        Gson gson = new Gson();
        String jsonAyatList = gson.toJson(surahListResponses.get(i).getListayat());
        surahEntity.setAyatListJson(jsonAyatList);

        surahEntityList.add(surahEntity);
      }

      Timber.d("SaveSurahList Save size is %s", surahEntityList.size());
      AppDatabase.getAppDatabase().surahDao().insertSurahAll(surahEntityList);
      return null;
    }
  }

  public interface DatabaseServiceCallback {
    void success(ArrayList<SurahListResponse> surahListResponses);
    void failed(NetworkError networkError);
  }

  //Update the submission count when surah successfully submitted
  public void updateSubmissionCount(String ayatId) {
    Timber.d("UpdateSubmissionCount %s", ayatId);
    new UpdateSubmissionAsync(ayatId).execute();
  }

  private static class UpdateSubmissionAsync extends AsyncTask<Void, Void, Void> {

    String ayatId;

    UpdateSubmissionAsync(String ayatId) {
      this.ayatId = ayatId;
    }

    @Override
    protected Void doInBackground(Void... voids) {
      Timber.d("doInBackground UpdateSubmissionCount");
      //Search the surah via id
      SurahEntity surahEntityList = AppDatabase.getAppDatabase().surahDao().getCurrentSubmissionData(ayatId);

      Gson gson = new Gson();

      //Get the Ayat List Json and convert to Object
      Type ayatListType = new TypeToken<List<AyatListsResponse>>(){}.getType();
      List<AyatListsResponse> ayatLists = gson.fromJson(surahEntityList.getAyatListJson(), ayatListType);

      //Looping the Ayat List Object for searching for same Id
      for (int j = 0; j < ayatLists.size(); j++) {
        if (ayatLists.get(j).getAyatId().equals(ayatId)) {
          //Found the same id, edit, and break loop
          ayatLists.get(j).setTotalSubmission(ayatLists.get(j).getTotalSubmission() + 1);
          break;
        }
      }

      //Change the ayatList back to string json
      String editedAyatList = gson.toJson(ayatLists);

      //Update the json
      AppDatabase.getAppDatabase().surahDao().updateSubmissionCount(editedAyatList, ayatId);
      return null;
    }
  }
}
