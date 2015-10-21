package message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by razvan on 21.10.2015.
 */
public class CreateQueueRequest extends Request {
  @Override
  public byte[] getBytes() throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    try {
      dataOutputStream.writeByte(Request.TYPE_CREATE_QUEUE);
      dataOutputStream.flush();
      return byteArrayOutputStream.toByteArray();
    } finally {
      dataOutputStream.close();
    }
  }
}
