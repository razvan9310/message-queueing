package message;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by razvan on 21.10.2015.
 */
public class MessageResponse extends Response {
  private String text;

  public MessageResponse(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }

  @Override
  public void send(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeByte(Response.TYPE_MESSAGE);
    dataOutputStream.writeUTF(text);
    dataOutputStream.flush();
  }
}