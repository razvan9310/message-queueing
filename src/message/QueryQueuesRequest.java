package message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by razvan on 21.10.2015.
 */
public class QueryQueuesRequest extends Request {
  private int receiver;

  public QueryQueuesRequest(int receiver) {
    this.receiver = receiver;
  }

  public int getReceiver() {
    return receiver;
  }

  @Override
  public byte[] getBytes() throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    try {
      dataOutputStream.writeByte(Request.TYPE_QUERY_QUEUES);
      dataOutputStream.writeInt(receiver);
      dataOutputStream.flush();
      return byteArrayOutputStream.toByteArray();
    } finally {
      dataOutputStream.close();
    }
  }
}
