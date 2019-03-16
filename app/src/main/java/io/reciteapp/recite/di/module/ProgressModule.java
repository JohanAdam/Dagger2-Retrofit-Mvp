package io.reciteapp.recite.di.module;

import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.progress.ProgressContract;
import io.reciteapp.recite.progress.ProgressDataManager;
import io.reciteapp.recite.progress.ProgressPresenter;

@Module
public class ProgressModule {

  @Provides
  @PerFragment
  public ProgressContract.Model provideProgressContractModel(
      SharedPreferencesManager sharedPreferencesManagerV) {
    return new ProgressDataManager(sharedPreferencesManagerV);
  }

  @Provides
  @PerFragment
  public ProgressContract.Presenter provideProgressContractPresenter(
      ProgressContract.Model model){
    return new ProgressPresenter(model);
  }

}
