package io.reciteapp.recite.di.module;

import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.recite.ReciteContract;
import io.reciteapp.recite.recite.ReciteDataManager;
import io.reciteapp.recite.recite.RecitePresenter;

@Module
public class ReciteModule {

  //initialized Recite Model
  @Provides
  @PerFragment
  public ReciteContract.Model provideReciteContractModel(
      SharedPreferencesManager sharedPreferencesManagerV) {
    return new ReciteDataManager(sharedPreferencesManagerV);
  }

  //initialized Recite Presenter
  @Provides
  @PerFragment
  public ReciteContract.Presenter provideReciteContractPresenter(
      ReciteContract.Model model) {
    return new RecitePresenter(model);
  }

}
