package server.database;

import java.util.concurrent.ThreadFactory;

/**
 * Created by razvan on 21.10.2015.
 */
public class DatabaseThreadFactory implements ThreadFactory {
  @Override
  public Thread newThread(Runnable r) {
    return new DatabaseThread(r);
  }
}
