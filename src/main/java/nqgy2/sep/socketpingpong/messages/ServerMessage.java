package nqgy2.sep.socketpingpong.messages;

public class ServerMessage extends Message{

  public String content;
  public String from;

  public ServerMessage(String content, String from) {
    this.content = content;
    this.from = from;
  }
}
