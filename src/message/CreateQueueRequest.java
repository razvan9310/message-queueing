package message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CreateQueueRequest extends Request {
  private int creator;

  public CreateQueueRequest(int creator) {
    this.creator = creator;
  }

  public int getCreator() {
    return creator;
  }

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
