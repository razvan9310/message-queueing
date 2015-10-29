package server.database;

import database.MessageHelper;

/**
 * Created by damachir on 10/29/15.
 */
public class CountMessagesTask extends DatabaseRunnable {
  private int messagesCount = 0;

  @Override
  public void run() {
    messagesCount = MessageHelper.countMessages(connection);
  }

  public int getMessagesCount() {
    return messagesCount;
  }
}
