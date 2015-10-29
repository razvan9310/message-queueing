package server.database;

import database.QueueHelper;
import logging.Logger;
import message.CreateQueueRequest;
import message.CreateQueueResponse;
import message.Request;
import server.ConnectionHandler;
import server.ResponseHandler;
import server.Server;

public class CreateQueueTask extends DatabaseRunnable {
  public CreateQueueTask(Request request, ConnectionHandler connectionHandler, Logger databaseResponseLogger) {
    super(request, connectionHandler, databaseResponseLogger);
  }

  @Override
  public void run() {
    Integer queue = QueueHelper.createQueue(
        connection, ((CreateQueueRequest) request).getCreator(), databaseResponseLogger);
    Server.clientExecutor.execute(new ResponseHandler(
        connectionHandler, new CreateQueueResponse(queue == null ? QueueHelper.NO_QUEUE : queue)));
  }
}
