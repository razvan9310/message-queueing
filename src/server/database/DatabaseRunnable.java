package server.database;

import java.sql.Connection;

public abstract class DatabaseRunnable implements Runnable {
  protected Connection connection;

  public void setConnection(Connection connection) {
    this.connection = connection;
  }
}
