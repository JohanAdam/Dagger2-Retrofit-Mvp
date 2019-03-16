package io.reciteapp.recite.root;

import dagger.Component;
import io.reciteapp.recite.di.component.ShareFragmentComponent;
import io.reciteapp.recite.di.component.VerseIdFragmentComponent;
import io.reciteapp.recite.di.module.ShareModule;
import io.reciteapp.recite.di.module.VerseIdModule;
import io.reciteapp.recite.services.ReciteFirebaseMessagingService;
import io.reciteapp.recite.data.networking.NetworkModule;
import io.reciteapp.recite.di.component.CsProfileFragmentComponent;
import io.reciteapp.recite.di.component.DashboardFragmentComponent;
import io.reciteapp.recite.di.component.HistoryDetailFragmentComponent;
import io.reciteapp.recite.di.component.HistoryListFragmentComponent;
import io.reciteapp.recite.di.component.LoginActivityComponent;
import io.reciteapp.recite.di.component.MainActivityComponent;
import io.reciteapp.recite.di.component.ProgressFragmentComponent;
import io.reciteapp.recite.di.component.ReciteFragmentComponent;
import io.reciteapp.recite.di.component.ReloadFragmentComponent;
import io.reciteapp.recite.di.component.SubmissionDetailCsFragmentComponent;
import io.reciteapp.recite.di.component.SubmissionDetailUFragmentComponent;
import io.reciteapp.recite.di.component.SubmissionListFragmentComponent;
import io.reciteapp.recite.di.component.SubmissionListFragmentCsComponent;
import io.reciteapp.recite.di.component.SurahListFragmentComponent;
import io.reciteapp.recite.di.module.CsProfileModule;
import io.reciteapp.recite.di.module.DashboardModule;
import io.reciteapp.recite.di.module.HistoryDetailModule;
import io.reciteapp.recite.di.module.HistoryListModule;
import io.reciteapp.recite.di.module.LoginModule;
import io.reciteapp.recite.di.module.MainActivityModule;
import io.reciteapp.recite.di.module.ProgressModule;
import io.reciteapp.recite.di.module.ReciteModule;
import io.reciteapp.recite.di.module.ReloadModule;
import io.reciteapp.recite.di.module.SubmissionDetailCsModule;
import io.reciteapp.recite.di.module.SubmissionDetailUModule;
import io.reciteapp.recite.di.module.SubmissionListCsModule;
import io.reciteapp.recite.di.module.SubmissionListModule;
import io.reciteapp.recite.di.module.SurahListModule;
import javax.inject.Singleton;

@Singleton
@Component(modules = {NetworkModule.class, ApplicationModule.class})
public interface ApplicationComponent {

//  void inject(DashboardFragment dashboardActivity);
//  void inject(MainActivity mainActivity);
    void inject(ReciteFirebaseMessagingService firebaseMessagingService);
//  SharedPreferencesContract sharedPreferencesContract();

  //Let dagger know that DashboardFragmentComponent is subcomponent of this component.
  MainActivityComponent newDashboardFragmentComponent(MainActivityModule mainActivityModule);

  DashboardFragmentComponent newDashboardFragmentComponent(DashboardModule mainModule);

  LoginActivityComponent newLoginActivityComponent (LoginModule loginModule);

  SurahListFragmentComponent newSurahListFragmentComponent(SurahListModule surahListModule);

  ReciteFragmentComponent newReciteFragmentComponent(ReciteModule reciteModule);

  HistoryListFragmentComponent newHistoryListFragmentComponent(HistoryListModule historyListModule);

  HistoryDetailFragmentComponent newHistoryDetailFragmentComponent(HistoryDetailModule historyDetailModule);

  CsProfileFragmentComponent newCsProfileFragmentComponent(CsProfileModule csProfileModule);

  ProgressFragmentComponent newProgressFragmentComponent(ProgressModule progressModule);

  SubmissionListFragmentComponent newSubmissionListFragmentComponent(SubmissionListModule submissionListModule);

  SubmissionListFragmentCsComponent newSubmissionListFragmentCsComponent(SubmissionListCsModule submissionListCsModule);

  SubmissionDetailUFragmentComponent newSubmissionDetailUComponent(SubmissionDetailUModule submissionDetailUModule);

  SubmissionDetailCsFragmentComponent newSubmissionDetailCsComponent(SubmissionDetailCsModule submissionDetailCsModule);

  ReloadFragmentComponent newReloadComponent(ReloadModule reloadModule);

  ShareFragmentComponent newShareComponent(ShareModule shareModule);

  VerseIdFragmentComponent newVerseIdComponent(VerseIdModule verseIdModule);

}
