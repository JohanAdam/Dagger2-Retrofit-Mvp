package io.reciteapp.recite.di.module;

import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.historylist.HistoryListContract;
import io.reciteapp.recite.historylist.HistoryListDataManager;
import io.reciteapp.recite.historylist.HistoryListPresenter;

@Module
public class HistoryListModule {

  //initialized HistoryList Model
  @Provides
  @PerFragment
  public HistoryListContract.Model provideHistoryListContractModel(
      SharedPreferencesManager sharedPreferencesManagerV) {
    return new HistoryListDataManager(sharedPreferencesManagerV);
  }

  //initialized HistoryList Presenter
  @Provides
  @PerFragment
  public HistoryListContract.Presenter provideHistoryListContractPresenter(
      HistoryListContract.Model model) {
    return new HistoryListPresenter(model);
  }

}
