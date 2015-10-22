package message;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Response {
  public static final byte TYPE_INVALID = 0;
  public static final byte TYPE_SENT_OK = 1;
  public static final byte TYPE_SENT_NOT_OK = 2;
  public static final byte TYPE_MESSAGE = 3;
  public static final byte TYPE_EMPTY = 4;
  public static final byte TYPE_QUEUE_CREATED = 5;
  public static final byte TYPE_QUEUE_DELETED = 6;
  public static final byte TYPE_QUEUE_QUERY = 7;

  public abstract void send(DataOutputStream dataOutputStream) throws IOException;
}
