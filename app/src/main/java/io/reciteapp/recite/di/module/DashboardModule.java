package io.reciteapp.recite.di.module;

import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.dashboard.DashboardContract;
import io.reciteapp.recite.dashboard.DashboardDataManager;
import io.reciteapp.recite.dashboard.DashboardPresenter;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;

@Module
public class DashboardModule {

  //initialized DashboardCall Model
  @Provides
//  @PerActivity
  public DashboardContract.Model provideDashboardContractModel(
      SharedPreferencesManager sharedPreferencesManagerV) {
    return new DashboardDataManager(sharedPreferencesManagerV);
  }

  //initialized DashboardCall Presenter
  @Provides
//  @PerActivity
  public DashboardContract.Presenter provideDashboardContractPresenter(
      DashboardContract.Model model) {
    return new DashboardPresenter(model);
  }

}
