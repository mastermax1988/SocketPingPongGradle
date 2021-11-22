package nqgy2.sep.socketpingpong.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import nqgy2.sep.socketpingpong.messages.ClientMessage;
import nqgy2.sep.socketpingpong.messages.RegistrationMessage;
import nqgy2.sep.socketpingpong.messages.ServerMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServerTest {

  private static Server server;
  private static Gson gson;
  private Connection c1;
  private Connection c2;

  @BeforeAll
  static void setUp() {
    gson = new Gson();
    server = new Server();
  }

  @BeforeEach
  void setUpConnection() {
    c1 = new Connection();
    c2 = new Connection();
    c1.socket = new Socket();
    c2.socket = new Socket();

    try {
      c1.socket.connect(new InetSocketAddress("127.0.0.1", 8080));
      c1.writer = new DataOutputStream(c1.socket.getOutputStream());
      c1.reader = new DataInputStream(c1.socket.getInputStream());
      c2.socket.connect(new InetSocketAddress("127.0.0.1", 8080));
      c2.writer = new DataOutputStream(c2.socket.getOutputStream());
      c2.reader = new DataInputStream(c2.socket.getInputStream());
    } catch (IOException e) {
      fail(e);
    }
  }

  @AfterEach
  void closeConnections() {
    try {
      c1.socket.close();
      c2.socket.close();
    } catch (Exception e) {

    }
  }

  @Test
  void testOneClient() {
    try {
      c1.writer.writeUTF(gson.toJson(new RegistrationMessage("alice")));
      c1.writer.writeUTF(gson.toJson(new ClientMessage("test")));
      String response = c1.reader.readUTF();
      String messageType = JsonParser.parseString(response).getAsJsonObject().get("messageType")
          .getAsString();
      assertEquals("servermsg", messageType);
      ServerMessage serverMessage = gson.fromJson(response, ServerMessage.class);
      assertEquals("test", serverMessage.content);
    } catch (IOException e) {
      fail(e);
    }
  }

  @Test
  void testTwoClients() {
    try {
      c1.writer.writeUTF(gson.toJson(new RegistrationMessage("alice")));
      c2.writer.writeUTF(gson.toJson(new RegistrationMessage("bob")));
      c1.writer.writeUTF(gson.toJson(new ClientMessage("hi bob")));
      String response_c1 = c1.reader.readUTF();
      String response_c2 = c2.reader.readUTF();
      ServerMessage serverMessage_c1 = gson.fromJson(response_c1, ServerMessage.class);
      ServerMessage serverMessage_c2 = gson.fromJson(response_c2, ServerMessage.class);
      assertEquals(serverMessage_c1.from, "alice");
      assertEquals(serverMessage_c2.from, "alice");
    } catch (IOException e) {
      fail(e);
    }
  }
}