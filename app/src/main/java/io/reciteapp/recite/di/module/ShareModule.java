package io.reciteapp.recite.di.module;

import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.share.ShareContract;
import io.reciteapp.recite.share.ShareDataManager;
import io.reciteapp.recite.share.SharePresenter;

@Module
public class ShareModule {

  @Provides
  @PerFragment
  public ShareContract.Model providesShareContractModel(
      SharedPreferencesManager sharedPreferencesManager) {
    return new ShareDataManager(sharedPreferencesManager);
  }

  @Provides
  @PerFragment
  public ShareContract.Presenter provideShareContractPresenter(
      ShareContract.Model model) {
    return new SharePresenter(model);
  }
}
