package nqgy2.sep.socketpingpong.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import nqgy2.sep.socketpingpong.messages.ClientMessage;
import nqgy2.sep.socketpingpong.messages.RegistrationMessage;
import nqgy2.sep.socketpingpong.messages.ServerMessage;


public class Client {

  private Socket socket;
  private ObjectInputStream reader;
  private ObjectOutputStream writer;
  private String name;

  public Client(String name) {
    this.name = name;
    try {
      socket = new Socket();
      socket.connect(new InetSocketAddress("127.0.0.1", 8080));
      writer = new ObjectOutputStream(socket.getOutputStream());
      reader = new ObjectInputStream(socket.getInputStream());
      Thread t = new Thread(this::listen);
      t.start();
      writer.writeObject(new RegistrationMessage(name));
      System.out.println("[CLIENT " + name + "] connected");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public synchronized void sendClientMessage(String s) {
    try {
      writer.writeObject(new ClientMessage(s));
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

  public void spam() {
    new Thread(
            () -> {
              while (true) {
                try {
                  writer.writeObject(new ClientMessage("SPAM"));
                } catch (IOException e) {
                  return;
                }
                try {
                  Thread.sleep(1000);
                } catch (InterruptedException e) {
                  return;
                }
              }
            })
        .start();
  }

  private void listen() {
    while (true) {
      try {
        Object msgObj = reader.readObject();
        if (msgObj instanceof ServerMessage) {
          ServerMessage message = (ServerMessage) msgObj;
          System.out.println(
              "[CLIENT " + name + "] received from: " + message.from + ": " + message.content);
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
}
