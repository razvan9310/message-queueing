package server.database;

import database.QueueHelper;
import logging.Logger;
import message.DeleteQueueRequest;
import message.DeleteQueueResponse;
import message.Request;
import server.ConnectionHandler;
import server.ResponseHandler;
import server.Server;

public class DeleteQueueTask extends DatabaseRunnable {
  public DeleteQueueTask(Request request, ConnectionHandler connectionHandler, Logger databaseResponseLogger) {
    super(request, connectionHandler, databaseResponseLogger);
  }

  @Override
  public void run() {
    boolean deleted = QueueHelper.deleteQueue(
        connection, ((DeleteQueueRequest) request).getQueue(), databaseResponseLogger);
    Server.clientExecutor.execute(new ResponseHandler(
        connectionHandler, new DeleteQueueResponse(deleted)));
  }
}
