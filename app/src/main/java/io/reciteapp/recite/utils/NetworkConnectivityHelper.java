package io.reciteapp.recite.utils;

import android.os.AsyncTask;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import timber.log.Timber;

public class NetworkConnectivityHelper extends AsyncTask<Void, Void, Boolean> {

  private NetworkConnectivityHelperCallback callback;

  //public constructor
  public NetworkConnectivityHelper(NetworkConnectivityHelperCallback callback) {
    this.callback = callback;
  }

  @Override
  protected Boolean doInBackground(Void... voids) {
    Timber.e("doInBackground");

    String address = "8.8.8.8";
    int port = 53;
    int timeoutMs = 5000; //Ms

    try {
      Socket socket = new Socket();
      SocketAddress socketAddress = new InetSocketAddress(address, port);

      socket.connect(socketAddress, timeoutMs);
      socket.close();

      return true;
    } catch (IOException e) {
      return false;
    }
  }

  @Override
  protected void onPostExecute(Boolean aBoolean) {
    super.onPostExecute(aBoolean);
    Timber.e("onPostExecute");
    Timber.d("Ping connectivity result %s", aBoolean);
    callback.onResult(aBoolean);
  }

  public interface NetworkConnectivityHelperCallback {
    void onResult(boolean result);
  }

}
