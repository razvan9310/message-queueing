package server.database;

import java.util.concurrent.FutureTask;

/**
 * Created by damachir on 11/1/15.
 */
public class DatabaseFutureTask<V> extends FutureTask<V> {
  private Runnable runnable;

  public DatabaseFutureTask(Runnable runnable, V result) {
    super(runnable, result);
    this.runnable = runnable;
  }

  public Runnable getRunnable() {
    return runnable;
  }
}
