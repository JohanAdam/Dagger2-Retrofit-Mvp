package io.reciteapp.recite.di.module;

import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.verseid.VerseIdContract;
import io.reciteapp.recite.verseid.VerseIdDataManager;
import io.reciteapp.recite.verseid.VerseIdPresenter;

@Module
public class VerseIdModule {

  @Provides
  @PerFragment
  public VerseIdContract.Model provideVerseIdContractModel(SharedPreferencesManager sharedPreferencesManager) {
    return new VerseIdDataManager(sharedPreferencesManager);
  }

  @Provides
  @PerFragment
  public VerseIdContract.Presenter provideVerseIdContractPresenter(VerseIdContract.Model model) {
    return new VerseIdPresenter(model);
  }

}
