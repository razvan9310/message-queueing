package message;

import java.io.DataOutputStream;
import java.io.IOException;

public class MessageResponse extends Response {
  private int sender;
  private String text;

  public MessageResponse(int sender, String text) {
    this.sender = sender;
    this.text = text;
  }

  public int getSender() {
    return sender;
  }

  public String getText() {
    return text;
  }

  @Override
  public void send(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeByte(Response.TYPE_MESSAGE);
    dataOutputStream.writeInt(sender);
    dataOutputStream.writeUTF(text);
    dataOutputStream.flush();
  }
}