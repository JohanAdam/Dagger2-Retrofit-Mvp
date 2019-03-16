package io.reciteapp.recite.di.component;

import dagger.Subcomponent;
import io.reciteapp.recite.dashboard.DashboardFragment;
import io.reciteapp.recite.di.module.DashboardModule;

//@PerActivity
@Subcomponent(modules = DashboardModule.class)
public interface DashboardFragmentComponent {

  void inject(DashboardFragment dashboardFragment);

}
