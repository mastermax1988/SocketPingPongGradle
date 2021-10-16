package nqgy2.sep.socketpingpong.shellclient;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import nqgy2.sep.socketpingpong.client.NetworkClient;
import nqgy2.sep.socketpingpong.messages.ServerMessage;

public class ShellUI implements PropertyChangeListener {

  private String name;
  private BufferedReader shellIn;
  private NetworkClient client;

  public ShellUI() {
    shellIn = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    System.out.println("Gib deinen Namen ein");
    try {
      name = shellIn.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    client = new NetworkClient(name);
    client.addPropertyChangeListener(this);
    Thread shellListenThread = new Thread(this::shellListener);
    shellListenThread.start();
  }

  private void shellListener() {
    while (true) {
      System.out.println("Deine Nachricht: ");
      String in;
      try {
        in = shellIn.readLine();
      } catch (IOException e) {
        return;
      }
      if (in.equals("!quit")) {
        return;
      } else {
        client.sendClientMessage(in);
      }
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName() != NetworkClient.NEW_MESSAGE_PROPERTY_NAME) {
      System.out.println("Uninteresting event received, ignored...");
      return;
    }
    String messageContent = ((ServerMessage) evt.getNewValue()).content;
    String from = ((ServerMessage) evt.getNewValue()).from;
    System.out.println("[CLIENT " + name + "] received from: " + from + ": " + messageContent);
  }
}
