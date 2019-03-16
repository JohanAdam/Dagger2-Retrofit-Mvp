package io.reciteapp.recite.di.component;

import dagger.Subcomponent;
import io.reciteapp.recite.di.module.ReciteModule;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.recite.ReciteFragment;

@PerFragment
@Subcomponent(modules = ReciteModule.class)
public interface ReciteFragmentComponent {

  void inject(ReciteFragment reciteFragment);

}
