package io.reciteapp.recite.data.sharedpref;

public interface SharedPreferencesContract {

  void saveString(String key, String data);

  void saveBoolean(String key, boolean data);

  void saveInt(String key, int data);

  String loadString(String key);

  boolean loadBoolean(String key);

  int loadingInt(String key);

  boolean loadLoginStatus();

  void clearData(String data);

  void logout();
}
