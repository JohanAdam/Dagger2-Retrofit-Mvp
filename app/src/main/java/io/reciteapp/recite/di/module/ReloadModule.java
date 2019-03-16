package io.reciteapp.recite.di.module;

import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.reload.ReloadContract;
import io.reciteapp.recite.reload.ReloadDataManager;
import io.reciteapp.recite.reload.ReloadPresenter;

@Module
public class ReloadModule {

  @Provides
  @PerFragment
  public ReloadContract.Model provideReloadContractModel(SharedPreferencesManager sharedPreferencesManager) {
    return new ReloadDataManager(sharedPreferencesManager);
  }

  @Provides
  @PerFragment
  public ReloadContract.Presenter provideReloadContractPresenter(ReloadContract.Model model) {
    return new ReloadPresenter(model);
  }

}
