package io.reciteapp.recite.di.component;

import dagger.Subcomponent;
import io.reciteapp.recite.di.module.SubmissionListModule;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.submissionlist.user.SubmissionListFragment;

@PerFragment
@Subcomponent(modules = SubmissionListModule.class)
public interface SubmissionListFragmentComponent {

  void inject(SubmissionListFragment submissionListFragment);

}
