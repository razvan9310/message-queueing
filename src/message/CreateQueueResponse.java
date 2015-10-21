package message;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by razvan on 21.10.2015.
 */
public class CreateQueueResponse extends Response {
  private int queue;

  public CreateQueueResponse(int queue) {
    this.queue = queue;
  }

  public int getQueue() {
    return queue;
  }

  @Override
  public void send(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeByte(Response.TYPE_QUEUE_CREATED);
    dataOutputStream.writeInt(queue);
    dataOutputStream.flush();
  }
}
