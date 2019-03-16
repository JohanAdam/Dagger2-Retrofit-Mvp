package io.reciteapp.recite.di.component;

import dagger.Subcomponent;
import io.reciteapp.recite.di.module.HistoryDetailModule;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.historydetail.HistoryDetailFragment;

@PerFragment
@Subcomponent(modules = HistoryDetailModule.class)
public interface HistoryDetailFragmentComponent {

  void inject(HistoryDetailFragment historyDetailFragment);

}
