package message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class MessageResponse extends Response {
  private int sender;
  private String text;
  private long timestamp;

  public MessageResponse(int sender, String text, long timestamp) {
    this.sender = sender;
    this.text = text;
    this.timestamp = timestamp;
  }

  public int getSender() {
    return sender;
  }

  public String getText() {
    return text;
  }

  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public void send(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeByte(Response.TYPE_MESSAGE);
    dataOutputStream.writeInt(sender);
    dataOutputStream.writeUTF(text);
    dataOutputStream.flush();
  }

  public static MessageResponse getOldestMessageResponse(List<MessageResponse> messageResponseList) {
    MessageResponse best = null;
    for (MessageResponse messageResponse : messageResponseList) {
      if (best == null || messageResponse.getTimestamp() < best.getTimestamp()) {
        best = messageResponse;
      }
    }
    return best;
  }
}