package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

public class MessageHelper {
  private static final Logger LOGGER = Logger.getLogger(MessageHelper.class.getName());

  private static final String PEEK_FROM_SENDER_QUERY =
      "SELECT * FROM peek_message_from_sender(?, ?)";
  private static final String PEEK_QUERY = "SELECT * FROM peek_queue(?, ?)";
  private static final String POP_QUERY = "SELECT * FROM pop_message(?, ?)";
  private static final String SEND_QUERY = "SELECT * FROM send_message(?, ?, ?)";
  private static final String SEND_WITH_RECEIVER_QUERY = "SELECT * FROM send_message(?, ?, ?, ?)";

  private static final String MESSAGES_COUNT_QUERY = "SELECT COUNT(*) FROM message";
  private static final int MESSAGES_COUNT_QUERY_RESULT_INDEX = 1;

  public static final double FAILED_MESSAGE_TIMESTAMP = -1.0;
  public static final int FAILED_COUNT_MESSAGES_QUERY_RESULT = -1;

  private enum MessageParameters { ID, SENDER_OR_QUEUE, TEXT, TIMESTAMP }
  private enum PeekFromSenderParameters { SENDER, RECEIVER }
  private enum PeekParameters { QUEUE, RECEIVER }
  private enum PopParameters { QUEUE, RECEIVER }
  private enum SendMessageParameters { TIMESTAMP }
  private enum SendParameters { SENDER, QUEUE, TEXT }
  private enum SendWithReceiverParameters { SENDER, RECEIVER, QUEUE, TEXT }

  private static Message getMessageFromQueryResults(ResultSet results, boolean addSenderInsteadOfQueue)
      throws SQLException {
    int id = results.getInt(MessageParameters.ID.ordinal() + 1);
    int sender = addSenderInsteadOfQueue ? results.getInt(MessageParameters.SENDER_OR_QUEUE.ordinal() + 1) : -1;
    int queue = addSenderInsteadOfQueue ? -1 : results.getInt(MessageParameters.SENDER_OR_QUEUE.ordinal() + 1);
    String text = results.getString(MessageParameters.TEXT.ordinal() + 1);
    double timestamp = results.getDouble(MessageParameters.TIMESTAMP.ordinal() + 1);
    return new Message(id, sender, queue, text, timestamp);
  }

  private static double getTimestampFromSendMessageResults(ResultSet results) throws SQLException {
    return results.getDouble(SendMessageParameters.TIMESTAMP.ordinal() + 1);
  }

  public static Message peekQueue(Connection connection, int queue, int receiver) {
    try {
      PreparedStatement peekStatement = connection.prepareStatement(PEEK_QUERY);
      peekStatement.setInt(PeekParameters.QUEUE.ordinal() + 1, queue);
      peekStatement.setInt(PeekParameters.RECEIVER.ordinal() + 1, receiver);
      ResultSet results = peekStatement.executeQuery();
      if (results.next()) {
        Message message = getMessageFromQueryResults(results, true);
        message.setQueue(queue);
        return message;
      }
    } catch (SQLException e) {
      LOGGER.severe("Failed to peek message: " + e.getMessage());
    }
    return null;
  }

  public static Message peekMessageFromSender(Connection connection, int sender, int receiver) {
    try {
      PreparedStatement peekStatement = connection.prepareStatement(PEEK_FROM_SENDER_QUERY);
      peekStatement.setInt(PeekFromSenderParameters.SENDER.ordinal() + 1, sender);
      peekStatement.setInt(PeekFromSenderParameters.RECEIVER.ordinal() + 1, receiver);
      ResultSet results = peekStatement.executeQuery();
      if (results.next()) {
        Message message = getMessageFromQueryResults(results, false);
        message.setSender(sender);
        return message;
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
        Message message = getMessageFromQueryResults(results, true);
        message.setQueue(queue);
        return message;
      }
    } catch (SQLException e) {
      LOGGER.severe("Failed to pop message: " + e.getMessage());
    }
    return null;
  }

  public static double sendMessage(Connection connection, int sender, int queue, String text) {
    try {
      PreparedStatement sendStatement = connection.prepareStatement(SEND_QUERY);
      sendStatement.setInt(SendParameters.SENDER.ordinal() + 1, sender);
      sendStatement.setInt(SendParameters.QUEUE.ordinal() + 1, queue);
      sendStatement.setString(SendParameters.TEXT.ordinal() + 1, text);
      ResultSet results = sendStatement.executeQuery();
      if (results.next()) {
        return getTimestampFromSendMessageResults(results);
      }
    } catch (SQLException e) {
      LOGGER.warning("Failed to send message: " + e.getMessage());
    }
    return FAILED_MESSAGE_TIMESTAMP;
  }

  public static double sendMessageWithReceiver(Connection connection, int sender, int receiver,
      int queue, String text) {
    try {
      PreparedStatement sendStatement = connection.prepareStatement(SEND_WITH_RECEIVER_QUERY);
      sendStatement.setInt(SendWithReceiverParameters.SENDER.ordinal() + 1, sender);
      sendStatement.setInt(SendWithReceiverParameters.RECEIVER.ordinal() + 1, receiver);
      sendStatement.setInt(SendWithReceiverParameters.QUEUE.ordinal() + 1, queue);
      sendStatement.setString(SendWithReceiverParameters.TEXT.ordinal() + 1, text);
      ResultSet results = sendStatement.executeQuery();
      if (results.next()) {
        return getTimestampFromSendMessageResults(results);
      }
    } catch (SQLException e) {
      LOGGER.warning("Failed to send message with receiver: " + e.getMessage());
    }
    return FAILED_MESSAGE_TIMESTAMP;
  }

  public static int countMessages(Connection connection) {
    try {
      Statement statement = connection.createStatement();
      ResultSet results = statement.executeQuery(MESSAGES_COUNT_QUERY);
      if (results.next()) {
        return results.getInt(MESSAGES_COUNT_QUERY_RESULT_INDEX);
      }
    } catch (SQLException e) {
      LOGGER.warning("Failed to query message count: " + e.getMessage());
    }
    return FAILED_COUNT_MESSAGES_QUERY_RESULT;
  }
}
