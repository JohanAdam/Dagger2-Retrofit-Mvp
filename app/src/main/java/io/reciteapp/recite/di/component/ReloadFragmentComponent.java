package io.reciteapp.recite.di.component;

import dagger.Subcomponent;
import io.reciteapp.recite.di.module.ReloadModule;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.reload.ReloadFragment;

@PerFragment
@Subcomponent(modules = ReloadModule.class)
public interface ReloadFragmentComponent {

  void inject(ReloadFragment reloadFragment);

}
