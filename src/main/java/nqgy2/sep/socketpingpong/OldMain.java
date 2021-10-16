package nqgy2.sep.socketpingpong;

import nqgy2.sep.socketpingpong.client.Client;
import nqgy2.sep.socketpingpong.server.Server;

public class OldMain {

  public static void main(String[] args) throws InterruptedException {
    Server server = new Server(); // server startet
    Client client1 = new Client("c1"); // client verbindet sich zum server

    client1.sendClientMessage("Nachricht 1");
    Thread.sleep(1000);

    Client client2 = new Client("c2"); // c2 verbindet sich
    Thread.sleep(1000);
    client2.sendClientMessage("Das ist ein längerer Text :)");

    Thread.sleep(1000);

    Thread.sleep(1000);
    client1.sendClientMessage("Wo bist du c2?");

    Thread.sleep(1000);
  }
}
