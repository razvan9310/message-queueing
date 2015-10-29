package server.database;

import logging.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DatabaseThreadPoolExecutor extends ThreadPoolExecutor {
  private Logger throughputLogger;
  private long startupTime;

  public DatabaseThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
      BlockingQueue<Runnable> workQueue, Logger throughputLogger) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
        new DatabaseThreadFactory());
    this.throughputLogger = throughputLogger;
    if (throughputLogger != null) {
      this.startupTime = System.nanoTime();
      throughputLogger.start();
    }
  }

  @Override
  protected void beforeExecute(Thread t, Runnable r) {
    DatabaseThread databaseThread = (DatabaseThread) t;
    DatabaseRunnable databaseRunnable = (DatabaseRunnable) r;
    databaseRunnable.setConnection(databaseThread.getConnection());
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    super.afterExecute(r, t);
    if (throughputLogger != null) {
      throughputLogger.log(String.valueOf(System.nanoTime() - startupTime));
    }
  }
}
