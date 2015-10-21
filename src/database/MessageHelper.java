package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by razvan on 21.10.2015.
 */
public class MessageHelper {
  private static final Logger LOGGER = Logger.getLogger(MessageHelper.class.getName());

  private static final String PEEK_FROM_SENDER_QUERY =
      "SELECT * FROM peek_message_from_sender(?, ?)";
  private static final String PEEK_QUERY = "SELECT * FROM peek_message(?, ?)";
  private static final String POP_QUERY = "SELECT * FROM pop_message(?, ?)";
  private static final String SEND_QUERY = "SELECT * FROM send_message(?, ?, ?)";
  private static final String SEND_WITH_RECEIVER_QUERY = "SELECT * FROM send_message(?, ?, ?, ?)";

  private enum MessageParameters { ID, SENDER, TEXT }
  private enum PeekFromSenderParameters { SENDER, RECEIVER }
  private enum PeekParameters { QUEUE, RECEIVER }
  private enum PopParameters { QUEUE, RECEIVER }
  private enum SendParameters { SENDER, QUEUE, TEXT }
  private enum SendWithReceiverParameters { SENDER, RECEIVER, QUEUE, TEXT }

  private static Message getMessageFromQueryResults(ResultSet results) throws SQLException {
    return new Message(results.getInt(MessageParameters.ID.ordinal() + 1),
        results.getInt(MessageParameters.SENDER.ordinal() + 1),
        results.getString(MessageParameters.TEXT.ordinal() + 1));
  }

  public static Message peekMessage(Connection connection, int queue, int receiver) {
    try {
      PreparedStatement popStatement = connection.prepareStatement(PEEK_QUERY);
      popStatement.setInt(PeekParameters.QUEUE.ordinal() + 1, queue);
      popStatement.setInt(PeekParameters.RECEIVER.ordinal() + 1, receiver);
      ResultSet results = popStatement.executeQuery();
      if (results.next()) {
        return getMessageFromQueryResults(results);
      }
    } catch (SQLException e) {
      LOGGER.severe("Failed to peek message: " + e.getMessage());
    }
    return null;
  }

  public static Message peekMessageFromSender(Connection connection, int sender, int receiver) {
    try {
      PreparedStatement popStatement = connection.prepareStatement(PEEK_FROM_SENDER_QUERY);
      popStatement.setInt(PeekFromSenderParameters.SENDER.ordinal() + 1, sender);
      popStatement.setInt(PeekFromSenderParameters.RECEIVER.ordinal() + 1, receiver);
      ResultSet results = popStatement.executeQuery();
      if (results.next()) {
        return getMessageFromQueryResults(results);
      }
    } catch (SQLException e) {
      LOGGER.severe("Failed to peek message from sender: " + e.getMessage());
    }
    return null;
  }

  public static Message popMessage(Connection connection, int queue, int receiver) {
    try {
      PreparedStatement popStatement = connection.prepareStatement(POP_QUERY);
      popStatement.setInt(PopParameters.QUEUE.ordinal() + 1, queue);
      popStatement.setInt(PopParameters.RECEIVER.ordinal() + 1, receiver);
      ResultSet results = popStatement.executeQuery();
      if (results.next()) {
        return getMessageFromQueryResults(results);
      }
    } catch (SQLException e) {
      LOGGER.severe("Failed to pop message: " + e.getMessage());
    }
    return null;
  }

  public static boolean sendMessage(Connection connection, int sender, int queue, String text) {
    try {
      PreparedStatement sendStatement = connection.prepareStatement(SEND_QUERY);
      sendStatement.setInt(SendParameters.SENDER.ordinal() + 1, sender);
      sendStatement.setInt(SendParameters.QUEUE.ordinal() + 1, queue);
      sendStatement.setString(SendParameters.TEXT.ordinal() + 1, text);
      sendStatement.execute();
      return true;
    } catch (SQLException e) {
      LOGGER.warning("Failed to send message: " + e.getMessage());
      return false;
    }
  }

  public static boolean sendMessageWithReceiver(Connection connection, int sender, int receiver,
      int queue, String text) {
    try {
      PreparedStatement sendStatement = connection.prepareStatement(SEND_WITH_RECEIVER_QUERY);
      sendStatement.setInt(SendWithReceiverParameters.SENDER.ordinal() + 1, sender);
      sendStatement.setInt(SendWithReceiverParameters.RECEIVER.ordinal() + 1, receiver);
      sendStatement.setInt(SendWithReceiverParameters.QUEUE.ordinal() + 1, queue);
      sendStatement.setString(SendWithReceiverParameters.TEXT.ordinal() + 1, text);
      sendStatement.execute();
      return true;
    } catch (SQLException e) {
      LOGGER.warning("Failed to send message with receiver: " + e.getMessage());
      return false;
    }
  }
}
