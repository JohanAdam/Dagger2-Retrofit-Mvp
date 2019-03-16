package io.reciteapp.recite.di.component;

import dagger.Subcomponent;
import io.reciteapp.recite.csprofile.CsProfileFragment;
import io.reciteapp.recite.di.module.CsProfileModule;
import io.reciteapp.recite.di.scope.PerFragment;

@PerFragment
@Subcomponent(modules = CsProfileModule.class)
public interface CsProfileFragmentComponent {

  void inject(CsProfileFragment csProfileFragment);

}
