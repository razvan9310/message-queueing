package message;

import java.io.IOException;

/**
 * Created by razvan on 21.10.2015.
 */
public abstract class Request {
  public static final byte TYPE_PEEK_QUEUE = 0;
  public static final byte TYPE_PEEK_SENDER = 1;
  public static final byte TYPE_POP = 2;
  public static final byte TYPE_SEND_NO_RECEIVER = 3;
  public static final byte TYPE_SEND_TO_RECEIVER = 4;
  public static final byte TYPE_CREATE_QUEUE = 5;
  public static final byte TYPE_DELETE_QUEUE = 6;
  public static final byte TYPE_QUERY_QUEUES = 7;

  public abstract byte[] getBytes() throws IOException;
}
