package io.reciteapp.recite.di.component;

import dagger.Subcomponent;
import io.reciteapp.recite.di.module.MainActivityModule;
import io.reciteapp.recite.di.scope.PerActivity;
import io.reciteapp.recite.main.MainActivity;

@PerActivity
@Subcomponent( modules = MainActivityModule.class )
public interface MainActivityComponent {

  void inject(MainActivity mainActivity);

}
