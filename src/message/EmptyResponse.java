package message;

import java.io.DataOutputStream;
import java.io.IOException;

public class EmptyResponse extends Response {
  @Override
  public void send(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeByte(Response.TYPE_EMPTY);
    dataOutputStream.flush();
  }
}
