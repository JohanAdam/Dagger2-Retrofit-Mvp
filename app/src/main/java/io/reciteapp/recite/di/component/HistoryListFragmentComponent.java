package io.reciteapp.recite.di.component;

import dagger.Subcomponent;
import io.reciteapp.recite.di.module.HistoryListModule;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.historylist.HistoryListFragment;

@PerFragment
@Subcomponent(modules = HistoryListModule.class)
public interface HistoryListFragmentComponent {

  void inject(HistoryListFragment historyListFragment);

}
