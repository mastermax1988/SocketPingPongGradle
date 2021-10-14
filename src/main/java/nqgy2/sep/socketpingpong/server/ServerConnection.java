package nqgy2.sep.socketpingpong.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import nqgy2.sep.socketpingpong.messages.Message;

public class ServerConnection {

  private Socket socket;
  private ObjectOutputStream writer;
  private ObjectInputStream reader;
  private String username;

  public ServerConnection(
      Socket socket, ObjectOutputStream writer, ObjectInputStream reader) {
    this.socket = socket;
    this.writer = writer;
    this.reader = reader;
  }

  synchronized void send(Message message) {
    try {
      writer.writeObject(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public ObjectInputStream getReader() {
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
