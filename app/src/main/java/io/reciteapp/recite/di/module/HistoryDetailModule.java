package io.reciteapp.recite.di.module;

import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.historydetail.HistoryDetailContract;
import io.reciteapp.recite.historydetail.HistoryDetailDataManager;
import io.reciteapp.recite.historydetail.HistoryDetailPresenter;

@Module
public class HistoryDetailModule {

  //initialized Model
  @Provides
  @PerFragment
  public HistoryDetailContract.Model provideHistoryDetailContractModel(
      SharedPreferencesManager sharedPreferencesManagerV) {
    return new HistoryDetailDataManager(sharedPreferencesManagerV);
  }

  //initialized Presenter
  @Provides
  @PerFragment
  public HistoryDetailContract.Presenter provideHistoryDetailContractPresenter(
      HistoryDetailContract.Model model) {
    return new HistoryDetailPresenter(model);
  }

}
