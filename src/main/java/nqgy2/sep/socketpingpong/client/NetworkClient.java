package nqgy2.sep.socketpingpong.client;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import nqgy2.sep.socketpingpong.messages.ClientMessage;
import nqgy2.sep.socketpingpong.messages.Message;
import nqgy2.sep.socketpingpong.messages.RegistrationMessage;
import nqgy2.sep.socketpingpong.messages.ServerMessage;

public class NetworkClient {

  public static final String NEW_MESSAGE_PROPERTY_NAME = "newMessage";
  private Socket socket;
  private DataInputStream reader;
  private DataOutputStream writer;
  private final PropertyChangeSupport propertyChangeSupport;
  private final String name;

  public NetworkClient(String name) {
    this.name = name;
    propertyChangeSupport = new PropertyChangeSupport(this);
    try {
      socket = new Socket();
      socket.connect(new InetSocketAddress("127.0.0.1", 8080));
      writer = new DataOutputStream(socket.getOutputStream());
      reader = new DataInputStream(socket.getInputStream());
      Thread t = new Thread(this::listen);
      t.start();

      writer.writeUTF(new Gson().toJson(new RegistrationMessage(name)));
      System.out.println("[CLIENT " + name + "] connected");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public synchronized void sendClientMessage(String s) {
    try {
      writer.writeUTF(new Gson().toJson(new ClientMessage(s)));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void disconnect() {
    try {
      System.out.println("[CLIENT " + name + "] disconnecting");
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void listen() {
    Gson gson = new Gson();
    while (true) {
      try {
        String msg = reader.readUTF();
        String msgType =
            JsonParser.parseString(msg).getAsJsonObject().get("messageType").getAsString();
        switch (msgType) {
          case Message.SERVERMESSAGE:
            ServerMessage message = gson.fromJson(msg, ServerMessage.class);
            propertyChangeSupport.firePropertyChange(NEW_MESSAGE_PROPERTY_NAME, null,  message);
            break;
          default:
            System.out.println("[CLIENT] received unknown message.");
        }
      } catch (IOException e) {
        System.out.println("[CLIENT " + name + "] connection lost");
        break; // stop listening
      }
    }
  }

  public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
  }

  public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
  }
}
