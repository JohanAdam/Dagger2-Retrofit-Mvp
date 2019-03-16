package io.reciteapp.recite.di.component;

import dagger.Subcomponent;
import io.reciteapp.recite.di.module.ShareModule;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.share.ShareFragment;

@PerFragment
@Subcomponent(modules = ShareModule.class)
public interface ShareFragmentComponent {

  void inject(ShareFragment shareFragment);

}
