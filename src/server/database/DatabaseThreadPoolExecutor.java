package server.database;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by razvan on 21.10.2015.
 */
public class DatabaseThreadPoolExecutor extends ThreadPoolExecutor {
  public DatabaseThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      TimeUnit unit, BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
        new DatabaseThreadFactory());
  }

  @Override
  protected void beforeExecute(Thread t, Runnable r) {
    DatabaseThread databaseThread = (DatabaseThread) t;
    DatabaseRunnable databaseRunnable = (DatabaseRunnable) r;
    databaseRunnable.setConnection(databaseThread.getConnection());
  }
}
