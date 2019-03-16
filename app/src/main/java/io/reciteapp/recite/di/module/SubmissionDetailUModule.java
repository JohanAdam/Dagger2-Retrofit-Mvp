package io.reciteapp.recite.di.module;

import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.submissiondetail.user.SubmissionDetailUContract;
import io.reciteapp.recite.submissiondetail.user.SubmissionDetailUDataManager;
import io.reciteapp.recite.submissiondetail.user.SubmissionDetailUPresenter;

@Module
public class SubmissionDetailUModule {

  @Provides
  @PerFragment
  public SubmissionDetailUContract.Model provideSubmissionDetailUContractModel(
      SharedPreferencesManager sharedPreferencesManagerV) {
    return new SubmissionDetailUDataManager(sharedPreferencesManagerV);
  }

  @Provides
  @PerFragment
  public SubmissionDetailUContract.Presenter provideSubmissionDetailUContractPresenter(
      SubmissionDetailUContract.Model model) {
    return new SubmissionDetailUPresenter(model);
  }


}
