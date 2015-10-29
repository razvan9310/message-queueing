package server.database;

import logging.Logger;
import message.Request;
import server.ConnectionHandler;

import java.sql.Connection;

public abstract class DatabaseRunnable implements Runnable {
  protected Connection connection;
  protected Request request;
  protected ConnectionHandler connectionHandler;
  protected Logger databaseResponseLogger;

  public DatabaseRunnable() {}

  public DatabaseRunnable(Request request, ConnectionHandler connectionHandler, Logger databaseResponseLogger) {
    this.request = request;
    this.connectionHandler = connectionHandler;
    this.databaseResponseLogger = databaseResponseLogger;
  }

  public void setConnection(Connection connection) {
    this.connection = connection;
  }
}
