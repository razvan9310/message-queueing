package server.database;

import database.MessageHelper;

/**
 * Created by damachir on 10/29/15.
 */
public class CountMessagesTask extends DatabaseRunnable {
  private int messageCount;

  @Override
  public void run() {
    messageCount = MessageHelper.countMessages(connection);
  }

  public int getMessageCount() {
    return messageCount;
  }
}
