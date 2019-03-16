package io.reciteapp.recite.di.module;

import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.di.scope.PerActivity;
import io.reciteapp.recite.main.MainContract;
import io.reciteapp.recite.main.MainDataManager;
import io.reciteapp.recite.main.MainPresenter;

@Module
public class MainActivityModule {

  @Provides
  @PerActivity
  public MainContract.Model provideMainModel(SharedPreferencesManager sharedPreferencesManagerV) {
    return new MainDataManager(sharedPreferencesManagerV);
  }

  @Provides
  @PerActivity
  public MainContract.Presenter provideMainPresenter(MainContract.Model model) {
    return new MainPresenter(model);
  }

}
