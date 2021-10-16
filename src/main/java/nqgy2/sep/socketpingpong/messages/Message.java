package nqgy2.sep.socketpingpong.messages;

import java.io.Serializable;

public abstract class Message implements Serializable {

  private static final long serialVersionUID = -1720220132260183694L;
  public static final String CLIENTMESSAGE = "clientmsg";
  public static final String SERVERMESSAGE = "servermsg";
  public static final String REGISTRATIONMESSAGE = "regmsg";

  public String messageType;

  public Message(String messageType) {
    this.messageType = messageType;
  }
}
