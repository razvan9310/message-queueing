package database;

import server.ServerMain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class QueueHelper {
  private static final Logger LOGGER = Logger.getLogger(QueueHelper.class.getName());

  private static final String CREATE_QUEUE_QUERY =
      "INSERT INTO queue(creator) VALUES (?) RETURNING ID";
  private static final String DELETE_QUEUE_QUERY = "DELETE FROM queue WHERE id=?";
  private static final String QUEUES_FOR_RECEIVER_QUERY = "SELECT * FROM query_queues(?)";

  private static final int CREATOR_INDEX = 1;
  private static final int QUEUE_INDEX = 1;
  private static final int RECEIVER_INDEX = 1;

  public static final int DEFAULT_CREATOR = 0;
  public static final int NO_QUEUE = -1;

  public static Integer createQueue(Connection connection, int creator, logging.Logger logger) {
    try {
      PreparedStatement createQueueStatement = connection.prepareStatement(CREATE_QUEUE_QUERY);
      createQueueStatement.setInt(CREATOR_INDEX, creator);
      long beforeQuery = System.nanoTime();
      ResultSet results = createQueueStatement.executeQuery();
      long afterQuery = System.nanoTime();
      long elapsed = afterQuery - beforeQuery;
      if (logger != null) {
        if (logger.getType() == logging.Logger.TYPE_DB_RESPONSE_TIME) {
          logger.log(String.valueOf(beforeQuery - ServerMain.startupTime) + " " + String.valueOf(elapsed) + "\n");
        } else if (logger.getType() == logging.Logger.TYPE_DB_THROUGHPUT) {
          logger.log(String.valueOf(afterQuery - ServerMain.startupTime) + "\n");
        }
      }
      if (results.next()) {
        return results.getInt(QUEUE_INDEX);
      } else {
        LOGGER.severe("Failed to create queue (unexpected result)");
        return null;
      }
    } catch (SQLException e) {
      LOGGER.severe("Failed to create queue: " + e.getMessage());
    }
    return null;
  }

  public static boolean deleteQueue(Connection connection, int queue, logging.Logger logger) {
    try {
      PreparedStatement deleteStatement = connection.prepareStatement(DELETE_QUEUE_QUERY);
      deleteStatement.setInt(QUEUE_INDEX, queue);
      long beforeQuery = System.nanoTime();
      deleteStatement.executeQuery();
      long afterQuery = System.nanoTime();
      long elapsed = afterQuery - beforeQuery;
      if (logger != null) {
        if (logger.getType() == logging.Logger.TYPE_DB_RESPONSE_TIME) {
          logger.log(String.valueOf(beforeQuery - ServerMain.startupTime) + " " + String.valueOf(elapsed) + "\n");
        } else if (logger.getType() == logging.Logger.TYPE_DB_THROUGHPUT) {
          logger.log(String.valueOf(afterQuery - ServerMain.startupTime) + "\n");
        }
      }
      return true;
    } catch (SQLException e) {
      LOGGER.severe("Failed to delete queue: " + e.getMessage());
    }
    return false;
  }

  public static ArrayList<Integer> queryQueuesForReceiver(Connection connection, int receiver, logging.Logger logger) {
    ArrayList<Integer> queues = new ArrayList<Integer>();
    try {
      PreparedStatement queryQueuesStatement = connection.prepareStatement(
          QUEUES_FOR_RECEIVER_QUERY);
      queryQueuesStatement.setInt(RECEIVER_INDEX, receiver);
      long beforeQuery = System.nanoTime();
      ResultSet results = queryQueuesStatement.executeQuery();
      long afterQuery = System.nanoTime();
      long elapsed = afterQuery - beforeQuery;
      if (logger != null) {
        if (logger.getType() == logging.Logger.TYPE_DB_RESPONSE_TIME) {
          logger.log(String.valueOf(beforeQuery - ServerMain.startupTime) + " " + String.valueOf(elapsed) + "\n");
        } else if (logger.getType() == logging.Logger.TYPE_DB_THROUGHPUT) {
          logger.log(String.valueOf(afterQuery - ServerMain.startupTime) + "\n");
        }
      }
      while (results.next()) {
        queues.add(results.getInt(QUEUE_INDEX));
      }
    } catch (SQLException e) {
      LOGGER.warning("Failed to retrieve queues for receiver: " + e.getMessage());
    }
    return queues;
  }
}
