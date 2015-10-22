package server;

import message.DeleteQueueRequest;
import message.InvalidRequestResponse;
import message.PeekQueueRequest;
import message.PeekSenderRequest;
import message.PopQueueRequest;
import message.QueryQueuesRequest;
import message.Request;
import message.SendMessageRequest;
import server.database.CreateQueueTask;
import server.database.DeleteQueueTask;
import server.database.PeekQueueTask;
import server.database.PeekSenderTask;
import server.database.PopQueueTask;
import server.database.QueryQueuesTask;
import server.database.SendMessageTask;
import utils.Types;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by razvan on 22.10.2015.
 */
public class RequestHandler implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

  private ConnectionHandler connectionHandler;
  private DataInputStream dataInputStream;

  public RequestHandler(ConnectionHandler connectionHandler, byte[] requestData, int messageSize) {
    this.connectionHandler = connectionHandler;
    dataInputStream = new DataInputStream(new ByteArrayInputStream(
        requestData, Types.INTEGER_BYTES, messageSize));
  }

  @Override
  public void run() {
    Runnable databaseTask;
    Request request;
    try {
      byte requestType = dataInputStream.readByte();
      switch (requestType) {
        case Request.TYPE_PEEK_QUEUE:
          request = new PeekQueueRequest(dataInputStream.readInt(), dataInputStream.readInt());
          databaseTask = new PeekQueueTask((PeekQueueRequest) request, connectionHandler);
          break;
        case Request.TYPE_PEEK_SENDER:
          request = new PeekSenderRequest(dataInputStream.readInt(), dataInputStream.readInt());
          databaseTask = new PeekSenderTask((PeekSenderRequest) request, connectionHandler);
          break;
        case Request.TYPE_POP:
          request = new PopQueueRequest(dataInputStream.readInt(), dataInputStream.readInt());
          databaseTask = new PopQueueTask((PopQueueRequest) request, connectionHandler);
          break;
        case Request.TYPE_SEND_NO_RECEIVER:
          request = new SendMessageRequest(dataInputStream.readInt(),
              SendMessageRequest.NO_RECEIVER, dataInputStream.readInt(), dataInputStream.readUTF());
          databaseTask = new SendMessageTask((SendMessageRequest) request, connectionHandler);
          break;
        case Request.TYPE_SEND_TO_RECEIVER:
          request = new SendMessageRequest(dataInputStream.readInt(), dataInputStream.readInt(),
              dataInputStream.readInt(), dataInputStream.readUTF());
          databaseTask = new SendMessageTask((SendMessageRequest) request, connectionHandler);
          break;
        case Request.TYPE_CREATE_QUEUE:
          databaseTask = new CreateQueueTask(connectionHandler);
          break;
        case Request.TYPE_DELETE_QUEUE:
          request = new DeleteQueueRequest(dataInputStream.readInt());
          databaseTask = new DeleteQueueTask((DeleteQueueRequest) request, connectionHandler);
          break;
        case Request.TYPE_QUERY_QUEUES:
          request = new QueryQueuesRequest(dataInputStream.readInt());
          databaseTask = new QueryQueuesTask((QueryQueuesRequest) request, connectionHandler);
          break;
        default:
          LOGGER.severe("Received unknown request type: " + requestType);
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
          InvalidRequestResponse response = new InvalidRequestResponse();
          response.send(dataOutputStream);
          connectionHandler.writeToSocketChannel(byteArrayOutputStream);
          return;
      }
      Server.databaseExecutor.execute(databaseTask);
    } catch (IOException e) {
      LOGGER.warning("Failed to read stream from client: " + e.getMessage());
    }
  }
}
