package nqgy2.sep.socketpingpong.shelldemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ShellUI {

  private String name;
  private BufferedReader shellIn;

  public ShellUI() {
    shellIn = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    System.out.println("Gib deinen Namen ein");
    try {
      name = shellIn.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    Thread shellListenThread = new Thread(this::shellListener);
    shellListenThread.start();
  }

  private void shellListener() {
    while (true) {
      System.out.println("gib was ein :)");
      String in;
      try {
        in = shellIn.readLine();
      } catch (IOException e) {
        return;
      }
      if (in.equals("!quit")) {
        return;
      } else {
        System.out.println(name + ", user input in upper case: " + in.toUpperCase());
      }
    }
  }
}
