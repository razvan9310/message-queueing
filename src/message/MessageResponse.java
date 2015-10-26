package message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class MessageResponse extends Response {
  private int sender;
  private int queue;
  private String text;
  private double timestamp;

  public MessageResponse(int sender, int queue, String text, double timestamp) {
    this.sender = sender;
    this.queue = queue;
    this.text = text;
    this.timestamp = timestamp;
  }

  public int getSender() {
    return sender;
  }

  public int getQueue() {
    return queue;
  }

  public String getText() {
    return text;
  }

  public double getTimestamp() {
    return timestamp;
  }

  @Override
  public void send(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeByte(Response.TYPE_MESSAGE);
    dataOutputStream.writeInt(sender);
    dataOutputStream.writeInt(queue);
    dataOutputStream.writeUTF(text);
    dataOutputStream.writeDouble(timestamp);
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