package message;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by razvan on 21.10.2015.
 */
public class DeleteQueueResponse extends Response {
  private boolean deletedSuccessfully;

  public DeleteQueueResponse(boolean deletedSuccessfully) {
    this.deletedSuccessfully = deletedSuccessfully;
  }

  public boolean isDeletedSuccessfully() {
    return deletedSuccessfully;
  }

  @Override
  public void send(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeByte(Response.TYPE_QUEUE_DELETED);
    dataOutputStream.writeBoolean(deletedSuccessfully);
    dataOutputStream.flush();
  }
}
