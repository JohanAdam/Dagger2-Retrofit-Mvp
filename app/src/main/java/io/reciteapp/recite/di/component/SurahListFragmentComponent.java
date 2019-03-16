package io.reciteapp.recite.di.component;

import dagger.Subcomponent;
import io.reciteapp.recite.di.module.SurahListModule;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.surahlist.SurahListFragment;

@PerFragment
@Subcomponent(modules = SurahListModule.class)
public interface SurahListFragmentComponent {

  void inject(SurahListFragment surahListFragment);

}
