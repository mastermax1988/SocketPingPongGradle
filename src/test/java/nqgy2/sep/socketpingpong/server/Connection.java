package nqgy2.sep.socketpingpong.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Connection {

  public Socket socket;
  public DataInputStream reader;
  public DataOutputStream writer;
}
