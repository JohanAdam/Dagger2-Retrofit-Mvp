package io.reciteapp.recite.di.component;

import dagger.Subcomponent;
import io.reciteapp.recite.di.module.SubmissionDetailCsModule;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.submissiondetail.cs.SubmissionDetailCsFragment;

@PerFragment
@Subcomponent(modules = SubmissionDetailCsModule.class)
public interface SubmissionDetailCsFragmentComponent {

  void inject(SubmissionDetailCsFragment submissionDetailCsFragment);

}
