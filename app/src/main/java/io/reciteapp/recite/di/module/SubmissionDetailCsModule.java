package io.reciteapp.recite.di.module;

import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.submissiondetail.cs.SubmissionDetailCsContract;
import io.reciteapp.recite.submissiondetail.cs.SubmissionDetailCsDataManager;
import io.reciteapp.recite.submissiondetail.cs.SubmissionDetailCsPresenter;

@Module
public class SubmissionDetailCsModule {

  @Provides
  @PerFragment
  public SubmissionDetailCsContract.Model provideSubmissionDetailCsContractModel(SharedPreferencesManager sharedPreferencesManagerV) {
    return new SubmissionDetailCsDataManager(sharedPreferencesManagerV);
  }

  @Provides
  @PerFragment
  public SubmissionDetailCsContract.Presenter provideSubmissionDetailCsContractPresenter(SubmissionDetailCsContract.Model model) {
    return new SubmissionDetailCsPresenter(model);
  }

}
