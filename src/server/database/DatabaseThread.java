package server.database;

import database.ConnectionHelper;

import java.sql.Connection;

/**
 * Created by razvan on 21.10.2015.
 */
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
