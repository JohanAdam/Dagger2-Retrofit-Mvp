package io.reciteapp.recite.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import io.reciteapp.recite.data.entity.SurahEntity;
import java.util.List;

/**
 * Dao
 *
 * It defines the method that access the database.
 * Annotations are user to bind SQL with each method
 * declared in DAO.
 */

@Dao
public interface SurahDao {

  //Get all surah
//  @Query("SELECT * FROM surah_table WHERE ayatListJson LIKE '%' || :ayatId  || '%'")
  @Query("SELECT * FROM surah_table")
  List<SurahEntity> getAllSurah();

  //Insert all surah in local db
  @Insert
  void insertSurahAll(List<SurahEntity> surahEntities);

  //Get submission data via ayatId for update submission count
  @Query("SELECT * FROM surah_table WHERE ayatListJson LIKE '%' || :ayatId || '%'")
  SurahEntity getCurrentSubmissionData(String ayatId);

  //Update submission count
  @Query("UPDATE surah_table SET ayatListJson = :updateJson WHERE ayatListJson LIKE '%' || :ayatId || '%'")
  void updateSubmissionCount(String updateJson, String ayatId);

  //All Surah count
  @Query("SELECT COUNT(*) FROM surah_table")
  int surahCount();

  //Search Surah in surah table
  @Query("SELECT * FROM surah_table WHERE surah_name LIKE :surahname")
  List<SurahEntity> searchSurah(String surahname);

  //Delete all Surah from surah
  @Query("DELETE FROM surah_table")
  void deleteAll();

}
