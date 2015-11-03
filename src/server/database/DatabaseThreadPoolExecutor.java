package server.database;

import logging.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RunnableFuture;
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
  protected <T>RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
    return new DatabaseFutureTask<>(runnable, value);
  }

  @Override
  protected void beforeExecute(Thread t, Runnable r) {
    DatabaseThread databaseThread = (DatabaseThread) t;
    DatabaseRunnable databaseRunnable = null;
    if (r instanceof DatabaseRunnable) {
      databaseRunnable = (DatabaseRunnable) r;
    } else if (r instanceof DatabaseFutureTask) {
      databaseRunnable = (DatabaseRunnable) ((DatabaseFutureTask) r).getRunnable();
    }
    if (databaseRunnable != null) {
      databaseRunnable.setConnection(databaseThread.getConnection());
    }
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    super.afterExecute(r, t);
    if (throughputLogger != null) {
      throughputLogger.log(String.valueOf(System.nanoTime() - startupTime) + "\n");
    }
  }
}
