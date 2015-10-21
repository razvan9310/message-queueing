package server.database;

import java.sql.Connection;

/**
 * Created by razvan on 21.10.2015.
 */
public abstract class DatabaseRunnable implements Runnable {
  private Connection connection;

  public void setConnection(Connection connection) {
    this.connection = connection;
  }
}
