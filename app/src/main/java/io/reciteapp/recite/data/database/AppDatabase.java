package io.reciteapp.recite.data.database;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.support.annotation.NonNull;
import io.reciteapp.recite.data.dao.SurahDao;
import io.reciteapp.recite.data.entity.SurahEntity;
import io.reciteapp.recite.root.App;
import timber.log.Timber;

@Database(entities = {SurahEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

  private static AppDatabase INSTANCE;

  public abstract SurahDao surahDao();

  public static AppDatabase getAppDatabase() {
    if (INSTANCE == null) {
      INSTANCE = Room.databaseBuilder(App.getApp(), AppDatabase.class, "surah-database")
          // allow queries on the main thread.
          // Dont do this on a real app! See PersistanceBasicSample for an example.
//          .allowMainThreadQueries()
          .build();
    }
    return INSTANCE;
  }

  public static void destroyInstance() {
    Timber.e("Destroy Instance");
    INSTANCE = null;
  }

  @NonNull
  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
    return null;
  }

  @NonNull
  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return null;
  }
}
