package io.reciteapp.recite.root;

import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import javax.inject.Singleton;

@Module
public class ApplicationModule {

  private App app;

  public ApplicationModule(App app) {
    this.app = app;
  }

  @Provides
  @Singleton
  public App provideApp(){
    return app;
  }

  @Provides
  @Singleton
  public SharedPreferencesManager provideSharedPreferencesManagerV(App app) {
    return new SharedPreferencesManager(app);
  }

}
