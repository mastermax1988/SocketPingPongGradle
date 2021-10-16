package nqgy2.sep.socketpingpong.messages;


public class RegistrationMessage extends Message {


  private static final long serialVersionUID = 4378182181556100022L;

  public String name;

  public RegistrationMessage(String name) {
    super(Message.REGISTRATIONMESSAGE);
    this.name = name;
  }
}
