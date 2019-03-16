package io.reciteapp.recite.data.networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import io.reciteapp.recite.BuildConfig;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.utils.OkHttpUtils;
import java.io.File;
import java.io.IOException;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

/**
 * Class that create network module that contain Retrofit builder and
 * provides NetworkService class and NetworkCallWrapper class
 */

@Module
public class NetworkModule {

  private File cacheFile;

  public NetworkModule(File cacheFile) {
    this.cacheFile = cacheFile;
  }

  @Provides
  @Singleton
  Retrofit provideCall() {
    Cache cache = null;
    try {
      cache = new Cache(cacheFile, 10 * 1024 * 1024);
    } catch (Exception e) {
      e.printStackTrace();
    }

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    if (BuildConfig.DEBUG) {
      // development build
      logging.setLevel(Level.BODY);
    } else {
      // production build
      logging.setLevel(Level.BASIC);
    }

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .addInterceptor(new Interceptor() {
          @Override
          public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();

            //Customize the request
            Request request = original.newBuilder()
                .header("Content-Type", "application/json")
                .removeHeader("Pragma")
                .header("Cache-Control", String.format("max-age=%d", BuildConfig.CACHETIME))
                .build();

            Response response = chain.proceed(request);
            response.cacheResponse();

            return response;
          }
        })
        .addInterceptor(logging)
        .cache(cache)
        .build();

    Gson gson = new GsonBuilder()
        .setLenient()
        .create();

    Timber.e("CREATED RETROFIT %s", new SharedPreferencesManager(App.getApp()).loadString(Constants.PREF_COUNTRY));
    return new Retrofit.Builder()
        .baseUrl(new SharedPreferencesManager(App.getApp()).loadString(Constants.PREF_COUNTRY).equals(Constants.my) ? BuildConfig.BASEURLMY : BuildConfig.BASEURLIN)
        .client(OkHttpUtils.createHttpClient())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build();
  }

  @Provides
  @Singleton
  @SuppressWarnings("unused")
  public NetworkService providesNetworkService(Retrofit retrofit) {
    return retrofit.create(NetworkService.class);
  }

  @Provides
  @Singleton
  @Named("service")
  @SuppressWarnings("unused")
  public NetworkCallWrapper providesService(NetworkService networkService) {
    return new NetworkCallWrapper(networkService);
  }

}
