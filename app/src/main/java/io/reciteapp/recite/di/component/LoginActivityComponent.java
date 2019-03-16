package io.reciteapp.recite.di.component;


import dagger.Subcomponent;
import io.reciteapp.recite.di.module.LoginModule;
import io.reciteapp.recite.di.scope.PerActivity;
import io.reciteapp.recite.login.LoginActivity;

@PerActivity
@Subcomponent(modules = LoginModule.class)
public interface LoginActivityComponent {

  void inject(LoginActivity loginActivity);

}
