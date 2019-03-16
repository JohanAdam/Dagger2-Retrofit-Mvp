package io.reciteapp.recite.utils;

import android.os.Environment;
import java.io.File;
import timber.log.Timber;

public class FileManager {

  /**
   * Create recite folder in internal
   * @return true is success, false if failed
   */
  public boolean createdReciteFolder() {
    File folder = new File(Environment.getExternalStorageDirectory() + "/.Recite");
    boolean success;
    if (!folder.exists()) {
      //Folder not exist, create
      success = folder.mkdir();
      if (success) {
        //success create folder
        return true;
      } else {
        //failed create folder
        return false;
      }
    } else {
      //Folder exist
      return true;
    }
  }

  /**
   * Get location File for saves audio recording
   * @param outputFileName surahName that will name the audio file
   * @return file with surah name should be save after recording.
   */
  public File getFile(String outputFileName) {
    return new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/.Recite/"
        + outputFileName + "_rec.mp3");
  }

  /**
   * Get direction of file URi to playing audio files
   * @param outputFileName surah name of the audio file name
   * @return direction of the file URi for playing
   */
  public String getFileUri(String outputFileName) {
    return Environment.getExternalStorageDirectory().getAbsolutePath() + "/.Recite/"
        + outputFileName + "_rec.mp3";
  }


  public boolean checkFileRecordFileExist(String fileName) {
    Timber.d("Check for file %s", fileName);
    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/.Recite/"
        + fileName + "_rec.mp3");
    if (dir.exists()) {
      Timber.d("Record file exist");
      return true;
    } else {
      Timber.d("Record file not exist");
      return false;
    }
  }


  public void deleteAll() {
    //remove all audio in the folder
    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/.Recite/");
    try {
      if (dir.isDirectory()) {
        String[] children = dir.list();
        for (String aChildren : children) {
          boolean deleted = new File(dir, aChildren).delete();
          Timber.e("Delete all audio in Recite folder. %s", deleted);
        }
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

  public void deleteFilebyFileName(String audioFileName) {
    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/.Recite/"
        + audioFileName + "_rec.mp3");
    boolean deleted = file.delete();
    Timber.e("Delete file byfileName " + file.getPath() + " deleted " + deleted);
  }

  public void deleteFilebyUri(String uri) {
    File file = new File(uri);
    boolean deleted = file.delete();
    Timber.e("Delete file byfileName " + file.getPath() + " uri " + uri  + " deleted " + deleted);
  }

}
