package nqgy2.sep.socketpingpong.shellclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import nqgy2.sep.socketpingpong.client.Client;

public class ShellUI {

  private String name;
  private BufferedReader shellIn;
  private Client client;

  public ShellUI() {
    shellIn = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    System.out.println("Gib deinen Namen ein");
    try {
      name = shellIn.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    client = new Client(name);
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
}
