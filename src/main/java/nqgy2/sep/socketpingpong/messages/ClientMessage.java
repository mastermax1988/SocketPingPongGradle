package nqgy2.sep.socketpingpong.messages;



public class ClientMessage extends Message {

  private static final long serialVersionUID = -2723363051271966964L;
  public String content;
  public ClientMessage(String content){
    super(Message.CLIENTMESSAGE);
    this.content = content;
  }
}
