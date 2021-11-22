package nqgy2.sep.socketpingpong.server;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import nqgy2.sep.socketpingpong.messages.ClientMessage;
import nqgy2.sep.socketpingpong.messages.Message;
import nqgy2.sep.socketpingpong.messages.RegistrationMessage;
import nqgy2.sep.socketpingpong.messages.ServerMessage;

public class Server {

  // über dieses serversocket werden die eingehenden verbindungen akzeptiert
  private ServerSocket serverSocket;

  // der einzige verbundene client kommuniziert über diesen socket mit diesem writer / reader mit
  // dem server -> für mehrere clients muss dies angepasst werden
  List<ServerConnection> connectedClients;

  public Server() {
    connectedClients = new ArrayList<>();
    try {
      serverSocket = new ServerSocket(8080);
      Thread t = new Thread(this::awaitConnection);
      t.start();
      System.out.println("[SERVER] waiting for client.Client");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void awaitConnection() {
    // hier wird im moment genau eine eingehende verbindung akzeptiert; ist der 1. client.Client verbunden,
    // dann wird dieser Thread beendet. für mehrere clients muss dies angepasst werden.
    while (true) {
      try {
        Socket socket = serverSocket.accept();
        ServerConnection connection =
            new ServerConnection(
                socket,
                new DataOutputStream(socket.getOutputStream()),
                new DataInputStream(socket.getInputStream()));

        connectedClients.add(connection);
        System.out.println("[SERVER]: client.Client connected");
        Thread t = new Thread(() -> handleMessages(connection));
        t.start();
      } catch (IOException e) {
        System.out.println("server.Server no longer accepting clients");
        break;
      }
    }
  }

  public void shutdown() {
    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleMessages(ServerConnection connection) {
    // dieser thread wartet auf ankommende nachrichten des verbundenen clients. bei mehreren clients
    // gibt es für jeden client genau einen thread, der auf nachrichten wartet.
    Gson gson = new Gson();
    while (connection.getSocket().isConnected()) {
      try {
        String jsonString = connection.getReader().readUTF();
        String messageType = JsonParser.parseString(jsonString).getAsJsonObject().get("messageType").getAsString();
        switch (messageType){
          case Message.REGISTRATIONMESSAGE:
            RegistrationMessage registrationMessage = gson.fromJson(jsonString, RegistrationMessage.class);
            connection.setUsername(registrationMessage.name);
            break;
          case Message.CLIENTMESSAGE:
            ClientMessage clientMessage = gson.fromJson(jsonString, ClientMessage.class);
            System.out.println("[SERVER] message received: " + clientMessage.content);
            sendToAllClients(
                gson.toJson(new ServerMessage("Pong - " + clientMessage.content, "From: " + connection.getUsername())));
            break;
          default:
            System.out.println("[SERVER] Unknown message");
        }
      } catch (IOException e) {
        System.out.println("[SERVER] connection lost " + connection.getUsername());
        connectedClients.remove(connection);
        break;
      }
    }
  }

  private void sendToAllClients(String msg) {
    for (ServerConnection c : connectedClients) {
      System.out.println("debug - sent msg to " + c.getUsername());
      c.send(msg);
    }
  }
}
