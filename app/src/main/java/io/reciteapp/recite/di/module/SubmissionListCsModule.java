package io.reciteapp.recite.di.module;

import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.submissionlist.cs.SubmissionListCsContract;
import io.reciteapp.recite.submissionlist.cs.SubmissionListCsDataManager;
import io.reciteapp.recite.submissionlist.cs.SubmissionListCsPresenter;

@Module
public class SubmissionListCsModule {

  @Provides
  @PerFragment
  public SubmissionListCsContract.Model provideSubmissionListCsContractModel(
      SharedPreferencesManager sharedPreferencesManagerV) {
    return new SubmissionListCsDataManager(sharedPreferencesManagerV);
  }

  @Provides
  @PerFragment
  public SubmissionListCsContract.Presenter provdeSubmissionListCsContractPresenter(
      SubmissionListCsContract.Model model) {
    return new SubmissionListCsPresenter(model);
  }

}
