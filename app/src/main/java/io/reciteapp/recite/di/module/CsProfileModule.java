package io.reciteapp.recite.di.module;

import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.csprofile.CsProfileContract;
import io.reciteapp.recite.csprofile.CsProfileDataManager;
import io.reciteapp.recite.csprofile.CsProfilePresenter;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.di.scope.PerFragment;

@Module
public class CsProfileModule {

  @Provides
  @PerFragment
  public CsProfileContract.Model provideCsProfileContractModel(
      SharedPreferencesManager sharedPreferencesManagerV) {
    return new CsProfileDataManager(sharedPreferencesManagerV);
  }

  @Provides
  @PerFragment
  public CsProfileContract.Presenter provideCsProfileContractPresenter(
      CsProfileContract.Model model) {
    return new CsProfilePresenter(model);
  }

}
