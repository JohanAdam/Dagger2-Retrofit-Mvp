package io.reciteapp.recite.di.module;

import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.surahlist.SurahListContract;
import io.reciteapp.recite.surahlist.SurahListDataManager;
import io.reciteapp.recite.surahlist.SurahListPresenter;

@Module
public class SurahListModule {

  //initialized SurahList Model
  @Provides
  @PerFragment
  public SurahListContract.Model provideSurahListContractModel(
      SharedPreferencesManager sharedPreferencesManagerV) {
    return new SurahListDataManager(sharedPreferencesManagerV);
  }

  //initialized SurahList Presenter
  @Provides
  @PerFragment
  public SurahListContract.Presenter provideSurahListContractPresenter(
      SurahListContract.Model model) {
    return new SurahListPresenter(model);
  }

}
