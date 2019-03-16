package io.reciteapp.recite.di.module;


import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.di.scope.PerActivity;
import io.reciteapp.recite.login.LoginContract;
import io.reciteapp.recite.login.LoginDataManager;
import io.reciteapp.recite.login.LoginPresenter;

@Module
public class LoginModule {

  @Provides
  @PerActivity
  public LoginContract.Model provideLoginContractModel(SharedPreferencesManager sharedPreferencesManagerV) {
    return new LoginDataManager(sharedPreferencesManagerV);
  }

  @Provides
  @PerActivity
  public LoginContract.Presenter provideLoginContractPresenter(LoginContract.Model model) {
    return new LoginPresenter(model);
  }

}
