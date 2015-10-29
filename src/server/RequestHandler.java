package server;

import message.*;
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

public class RequestHandler implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

  private ConnectionHandler connectionHandler;
  private DataInputStream dataInputStream;
  private logging.Logger databaseResponseLogger;

  public RequestHandler(ConnectionHandler connectionHandler, byte[] requestData, int messageSize,
      logging.Logger databaseResponseLogger) {
    this.connectionHandler = connectionHandler;
    dataInputStream = new DataInputStream(new ByteArrayInputStream(
        requestData, Types.INTEGER_BYTES, messageSize));
    this.databaseResponseLogger = databaseResponseLogger;
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
          databaseTask = new PeekQueueTask(request, connectionHandler, databaseResponseLogger);
          break;
        case Request.TYPE_PEEK_SENDER:
          request = new PeekSenderRequest(dataInputStream.readInt(), dataInputStream.readInt());
          databaseTask = new PeekSenderTask(request, connectionHandler, databaseResponseLogger);
          break;
        case Request.TYPE_POP:
          request = new PopQueueRequest(dataInputStream.readInt(), dataInputStream.readInt());
          databaseTask = new PopQueueTask(request, connectionHandler, databaseResponseLogger);
          break;
        case Request.TYPE_SEND_NO_RECEIVER:
          request = new SendMessageRequest(dataInputStream.readInt(),
              SendMessageRequest.NO_RECEIVER, dataInputStream.readInt(), dataInputStream.readUTF());
          databaseTask = new SendMessageTask(request, connectionHandler, databaseResponseLogger);
          break;
        case Request.TYPE_SEND_TO_RECEIVER:
          request = new SendMessageRequest(dataInputStream.readInt(), dataInputStream.readInt(),
              dataInputStream.readInt(), dataInputStream.readUTF());
          databaseTask = new SendMessageTask(request, connectionHandler, databaseResponseLogger);
          break;
        case Request.TYPE_CREATE_QUEUE:
          request = new CreateQueueRequest(dataInputStream.readInt());
          databaseTask = new CreateQueueTask(request, connectionHandler, databaseResponseLogger);
          break;
        case Request.TYPE_DELETE_QUEUE:
          request = new DeleteQueueRequest(dataInputStream.readInt());
          databaseTask = new DeleteQueueTask(request, connectionHandler, databaseResponseLogger);
          break;
        case Request.TYPE_QUERY_QUEUES:
          request = new QueryQueuesRequest(dataInputStream.readInt());
          databaseTask = new QueryQueuesTask(request, connectionHandler, databaseResponseLogger);
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
