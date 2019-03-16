package io.reciteapp.recite.di.component;

import dagger.Subcomponent;
import io.reciteapp.recite.di.module.VerseIdModule;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.verseid.VerseIdFragment;

@PerFragment
@Subcomponent(modules = VerseIdModule.class)
public interface VerseIdFragmentComponent {

  void inject(VerseIdFragment verseIdFragment);

}
