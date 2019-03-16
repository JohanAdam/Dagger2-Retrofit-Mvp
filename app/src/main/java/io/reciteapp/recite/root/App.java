package io.reciteapp.recite.root;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import io.fabric.sdk.android.Fabric;
import io.reciteapp.recite.BuildConfig;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.networking.NetworkModule;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import java.io.File;
import timber.log.Timber;

public class App extends Application {

  private static App app;

  public static App getApp() {
    return app;
  }

  public ApplicationComponent applicationComponent;

  public ApplicationComponent getApplicationComponent() {
    return applicationComponent;
  }

  public RefWatcher refWatcher;

  @Override
  public void onCreate() {
    super.onCreate();
    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return;
    }
    refWatcher = LeakCanary.install(this);
    LeakCanary.enableDisplayLeakActivity(this);
    app = this;

    //Initalized Fabric
    Fabric.with(this, new Crashlytics());

    //Initialized Crashlytics
    CrashlyticsCore core = new CrashlyticsCore.Builder()
        .disabled(BuildConfig.DEBUG)
        .build();
    Fabric.with(this, new Crashlytics.Builder().core(core).build());

    //Initialized Timber
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }
    Timber.plant(new CrashlyticsTree());

    FacebookSdk.sdkInitialize(getApplicationContext());
    AppEventsLogger.activateApp(this);

    buildComponent();

    new SharedPreferencesManager(this).getLanguage();
  }

  public void mustDie(Object object) {
    if (refWatcher != null) {
      refWatcher.watch(object);
    }
  }

  public void buildComponent() {
    Timber.e("BUILD COMPONENT %s", new SharedPreferencesManager(this).loadString(
        Constants.PREF_COUNTRY));
    File cacheFile = new File(getCacheDir(), "response");

    applicationComponent = DaggerApplicationComponent.builder()
        .networkModule(new NetworkModule(cacheFile))
        .applicationModule(new ApplicationModule(this))
        .build();
  }

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  public class CrashlyticsTree extends Timber.Tree {

    private static final String CRASHLYTICS_KEY_PRIORITY = "priority";
    private static final String CRASHLYTICS_KEY_TAG = "tag";
    private static final String CRASHLYTICS_KEY_MESSAGE = "message";

    @Override
    protected void log(int priority, @Nullable String tag, @Nullable String message,
        @Nullable Throwable t) {
      if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
        return;
      }

      Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority);
      Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag);
      Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message);

      if (t == null) {
        Crashlytics.logException(new Exception(message));
      } else {
        Crashlytics.logException(t);
      }
    }
  }


//  package io.reciteapp.recite.deps;
//
//import dagger.Component;
//import io.reciteapp.recite.dashboard.DashboardFragment;
//import io.reciteapp.recite.data.networking.NetworkModule;
//import javax.inject.Singleton;
//
//  @Singleton
//  @Component(modules = {NetworkModule.class})
//  public interface Deps {
//
//    void inject(DashboardFragment dashboardActivity);
//
//  }
}
