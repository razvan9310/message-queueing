package message;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by razvan on 21.10.2015.
 */
public class InvalidRequestResponse extends Response {
  @Override
  public void send(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeByte(Response.TYPE_INVALID);
    dataOutputStream.flush();
  }
}
