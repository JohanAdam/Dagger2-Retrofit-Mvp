package io.reciteapp.recite.di.component;

import dagger.Subcomponent;
import io.reciteapp.recite.di.module.ProgressModule;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.progress.ProgressFragment;

@PerFragment
@Subcomponent(modules = ProgressModule.class)
public interface ProgressFragmentComponent {

  void inject(ProgressFragment progressFragment);

}
