package message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by razvan on 21.10.2015.
 */
public class PeekQueueRequest extends Request {
  private int receiver;
  private int queue;

  public PeekQueueRequest(int receiver, int queue) {
    this.receiver = receiver;
    this.queue = queue;
  }

  public int getReceiver() {
    return receiver;
  }

  public int getQueue() {
    return queue;
  }

  @Override
  public byte[] getBytes() throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    try {
      dataOutputStream.writeByte(Request.TYPE_PEEK_QUEUE);
      dataOutputStream.writeInt(receiver);
      dataOutputStream.writeInt(queue);
      dataOutputStream.flush();
      return byteArrayOutputStream.toByteArray();
    } finally {
      dataOutputStream.close();
    }
  }
}
