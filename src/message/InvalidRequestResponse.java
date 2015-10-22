package message;

import java.io.DataOutputStream;
import java.io.IOException;

public class InvalidRequestResponse extends Response {
  @Override
  public void send(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeByte(Response.TYPE_INVALID);
    dataOutputStream.flush();
  }
}
