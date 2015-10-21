package message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by razvan on 21.10.2015.
 */
public class SendMessageRequest extends Request {
  public static final int NO_RECEIVER = -1;

  private int sender;
  private int receiver;
  private int queue;
  private String text;

  public SendMessageRequest(int sender, int receiver, int queue, String text) {
    this.sender = sender;
    this.receiver = receiver;
    this.queue = queue;
    this.text = text;
  }

  public int getSender() {
    return sender;
  }

  public int getReceiver() {
    return receiver;
  }

  public int getQueue() {
    return queue;
  }

  public String getText() {
    return text;
  }

  @Override
  public byte[] getBytes() throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    try {
      if (receiver == NO_RECEIVER) {
        dataOutputStream.writeByte(Request.TYPE_SEND_NO_RECEIVER);
        dataOutputStream.writeInt(this.sender);
        dataOutputStream.writeInt(this.queue);
        dataOutputStream.writeUTF(this.text);
      } else {
        dataOutputStream.writeByte(Request.TYPE_SEND_TO_RECEIVER);
        dataOutputStream.writeInt(this.sender);
        dataOutputStream.writeInt(this.receiver);
        dataOutputStream.writeInt(this.queue);
        dataOutputStream.writeUTF(this.text);
      }
      dataOutputStream.flush();
      return byteArrayOutputStream.toByteArray();
    } finally {
      dataOutputStream.close();
    }
  }
}
