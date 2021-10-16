package nqgy2.sep.socketpingpong.client;

import com.google.gson.Gson;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import nqgy2.sep.socketpingpong.messages.ClientMessage;
import nqgy2.sep.socketpingpong.messages.RegistrationMessage;
import nqgy2.sep.socketpingpong.messages.ServerMessage;

public class Client {

  public static final String NEW_MESSAGE_PROPERTY_NAME = "newMessage";
  private Socket socket;
  private ObjectInputStream reader;
  private ObjectOutputStream writer;
  private PropertyChangeSupport propertyChangeSupport;
  private String name;

  public Client(String name) {
    this.name = name;
    propertyChangeSupport = new PropertyChangeSupport(this);
    try {
      socket = new Socket();
      socket.connect(new InetSocketAddress("127.0.0.1", 8080));
      writer = new ObjectOutputStream(socket.getOutputStream());
      reader = new ObjectInputStream(socket.getInputStream());
      Thread t = new Thread(this::listen);
      t.start();

      writer.writeObject(new Gson().toJson(new RegistrationMessage(name)));
      System.out.println("[CLIENT " + name + "] connected");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public synchronized void sendClientMessage(String s) {
    try {
      writer.writeObject(new Gson().toJson(new ClientMessage(s)));
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
    while (true) {
      try {
        Object msgObj = reader.readObject();
        if (msgObj instanceof ServerMessage) {
          ServerMessage message = (ServerMessage) msgObj;
          propertyChangeSupport.firePropertyChange(NEW_MESSAGE_PROPERTY_NAME, null, msgObj);
        } else {
          System.out.println("[CLIENT] received unknown message.");
        }
      } catch (IOException e) {
        System.out.println("[CLIENT " + name + "] connection lost");
        break; // stop listening
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
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
