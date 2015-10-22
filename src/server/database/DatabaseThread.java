package server.database;

import database.ConnectionHelper;

import java.sql.Connection;

public class DatabaseThread extends Thread {
  private Connection connection;

  public DatabaseThread(Runnable r) {
    super(r);
    connection = ConnectionHelper.getConnection();
  }

  public Connection getConnection() {
    return connection;
  }
}
