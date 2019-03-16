package io.reciteapp.recite.di.module;

import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.submissionlist.user.SubmissionListContract;
import io.reciteapp.recite.submissionlist.user.SubmissionListDataManager;
import io.reciteapp.recite.submissionlist.user.SubmissionListPresenter;

@Module
public class SubmissionListModule {

  @Provides
  @PerFragment
  public SubmissionListContract.Model provideSubmissionListContractModel(
      SharedPreferencesManager sharedPreferencesManagerV) {
    return new SubmissionListDataManager(sharedPreferencesManagerV);
  }

  @Provides
  @PerFragment
  public SubmissionListContract.Presenter provideSubmissionListContractPresenter(
      SubmissionListContract.Model model) {
    return new SubmissionListPresenter(model);
  }

}
