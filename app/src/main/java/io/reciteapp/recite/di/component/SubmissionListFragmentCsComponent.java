package io.reciteapp.recite.di.component;

import dagger.Subcomponent;
import io.reciteapp.recite.di.module.SubmissionListCsModule;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.submissionlist.cs.SubmissionListCsFragment;

@PerFragment
@Subcomponent(modules = SubmissionListCsModule.class)
public interface SubmissionListFragmentCsComponent {

  void inject(SubmissionListCsFragment submissionListCsFragment);

}
