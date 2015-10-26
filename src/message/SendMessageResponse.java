package message;

import database.MessageHelper;

import java.io.DataOutputStream;
import java.io.IOException;

public class SendMessageResponse extends Response {
  private long arrivalTimestamp;

  public SendMessageResponse(long arrivalTimestamp) {
    this.arrivalTimestamp = arrivalTimestamp;
  }

  public boolean isSentSuccessfully() {
    return arrivalTimestamp != MessageHelper.FAILED_MESSAGE_TIMESTAMP;
  }

  public long getArrivalTimestamp() {
    return arrivalTimestamp;
  }

  @Override
  public void send(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeByte(
        isSentSuccessfully() ? Response.TYPE_SENT_OK : Response.TYPE_SENT_NOT_OK);
    dataOutputStream.flush();
  }
}
