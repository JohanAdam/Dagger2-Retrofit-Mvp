package io.reciteapp.recite.surahlist;

import io.reciteapp.recite.data.model.SurahListResponse;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.repository.SurahRepository;
import io.reciteapp.recite.data.repository.SurahRepository.DatabaseServiceCallback;
import io.reciteapp.recite.surahlist.SurahListContract.Model;
import io.reciteapp.recite.surahlist.SurahListContract.Presenter;
import io.reciteapp.recite.surahlist.SurahListContract.View;
import java.util.ArrayList;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class SurahListPresenter implements Presenter {

  private View view;
  private Model model;
  private CompositeSubscription subscriptions;
  private SurahRepository databaseService;

  /**
   * Initialized Presenter
   * @param model Set model to presenter
   */
  public SurahListPresenter(Model model) {
    this.model = model;
  }

  /**
   * Set view and NetworkService
   * @param view Set view to presenter
   * @param service Set NetworkService to presenter
   */
  @Override
  public void setView(View view, NetworkCallWrapper service) {
    this.subscriptions = new CompositeSubscription();
    this.view = view;
    databaseService = new SurahRepository(model.getToken(), service);
  }

  /**
   * Check for view after a network call to avoid view null when result return from call
   * @return true for view is attach, false for view is null
   */
  private boolean isViewAttached() {
    return view != null;
  }

  /**
   * Get SurahList from server
   */
  @Override
  public void getSurahList() {
    view.showWait();
    view.removeErrorLayout();
    view.removeNoResultLayout();
    view.removeSurahList();

    databaseService.getSurahList(new DatabaseServiceCallback() {
      @Override
      public void success(ArrayList<SurahListResponse> surahListResponses) {
        Timber.d("Success size receive is %s", surahListResponses.size());
        view.removeWait();
        //bind downloaded surahlist to rv
        view.setSurahList(surahListResponses);
      }

      @Override
      public void failed(NetworkError networkError) {
        Timber.e("Failed networkError %s", networkError.getResponseCode());
        view.removeWait();
        view.removeSurahList();
        view.showErrorLayout();
      }
    });
  }

  /**
   * Filtered list by text insert by user
   * @param searchText text search by user
   */
  @Override
  public void filteredList(String searchText) {
    if (isViewAttached()) {
      view.removeErrorLayout();
      view.removeNoResultLayout();
      view.removeSurahList();

      //get main list
      ArrayList<SurahListResponse> surahList = view.getSurahListMain();

      //trim and make it lower case for easier to search
      searchText = searchText.toLowerCase().trim();

      final ArrayList<SurahListResponse> filteredModelList = new ArrayList<>();

      if (surahList != null && !surahList.isEmpty()) {
        for (SurahListResponse data : surahList) {
          String surahName = data.getSurahName().toLowerCase().trim();
          if (surahName.contains(searchText)) {
            filteredModelList.add(data);
          }
        }

        Timber.d("size filteredModelList is %s", filteredModelList.size());
        if (!filteredModelList.isEmpty()) {
          view.setSurahList(filteredModelList);
        } else {
          view.showNoResultLayout();
        }
      } else {
        view.removeNoResultLayout();
      }
    }
  }

  /**
   * Unsubscribe all Network Call
   */
  @Override
  public void unSubscribe() {
    databaseService.removeSubscription();
    subscriptions.unsubscribe();
    view = null;
  }
}
