package message;

import java.io.DataOutputStream;
import java.io.IOException;

public class SendMessageResponse extends Response {
  private boolean sentSuccessfully;

  public SendMessageResponse(boolean sentSuccessfully) {
    this.sentSuccessfully = sentSuccessfully;
  }

  public boolean isSentSuccessfully() {
    return sentSuccessfully;
  }

  @Override
  public void send(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeByte(
        sentSuccessfully ? Response.TYPE_SENT_OK : Response.TYPE_SENT_NOT_OK);
    dataOutputStream.flush();
  }
}
