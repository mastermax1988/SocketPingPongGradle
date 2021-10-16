package nqgy2.sep.socketpingpong.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerConnection {

  private Socket socket;
  private DataOutputStream writer;
  private DataInputStream reader;
  private String username;

  public ServerConnection(
      Socket socket, DataOutputStream writer, DataInputStream reader) {
    this.socket = socket;
    this.writer = writer;
    this.reader = reader;
  }

  synchronized void send(String message) {
    try {
      writer.writeUTF(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public DataInputStream getReader() {
    return reader;
  }

  public Socket getSocket() {
    return socket;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
