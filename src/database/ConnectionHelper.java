package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class ConnectionHelper {
  private static final Logger LOGGER = Logger.getLogger(ConnectionHelper.class.getName());
  private static final String DATABASE_URL_ENV_VAR_NAME = "DATABASE_URL";

  public static Connection getConnection() {
    String url = System.getenv(DATABASE_URL_ENV_VAR_NAME);
    try {
      return DriverManager.getConnection(url);
    } catch (SQLException e) {
      LOGGER.severe("Failed to get database connection: " + e.getMessage());
    }
    return null;
  }
}
