package server.database;

import database.QueueHelper;
import message.CreateQueueRequest;
import message.CreateQueueResponse;
import server.ConnectionHandler;
import server.ResponseHandler;
import server.Server;

public class CreateQueueTask extends DatabaseRunnable {
  private CreateQueueRequest request;
  private ConnectionHandler connectionHandler;

  public CreateQueueTask(CreateQueueRequest request, ConnectionHandler connectionHandler) {
    this.request = request;
    this.connectionHandler = connectionHandler;
  }

  @Override
  public void run() {
    Integer queue = QueueHelper.createQueue(connection, request.getCreator());
    Server.clientExecutor.execute(new ResponseHandler(
        connectionHandler, new CreateQueueResponse(queue == null ? QueueHelper.NO_QUEUE : queue)));
  }
}
