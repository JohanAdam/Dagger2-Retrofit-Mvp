package io.reciteapp.recite.di.component;

import dagger.Subcomponent;
import io.reciteapp.recite.di.module.SubmissionDetailUModule;
import io.reciteapp.recite.di.scope.PerFragment;
import io.reciteapp.recite.submissiondetail.user.SubmissionDetailUFragment;

@PerFragment
@Subcomponent(modules = SubmissionDetailUModule.class)
public interface SubmissionDetailUFragmentComponent {

  void inject(SubmissionDetailUFragment submissionDetailUFragment);

}
