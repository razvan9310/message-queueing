package server.database;

import java.util.concurrent.ThreadFactory;

public class DatabaseThreadFactory implements ThreadFactory {
  @Override
  public Thread newThread(Runnable r) {
    return new DatabaseThread(r);
  }
}
