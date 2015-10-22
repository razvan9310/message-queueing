package server.database;

import database.QueueHelper;
import message.CreateQueueResponse;
import server.ConnectionHandler;
import server.ResponseHandler;
import server.Server;

public class CreateQueueTask extends DatabaseRunnable {
  private ConnectionHandler connectionHandler;

  public CreateQueueTask(ConnectionHandler connectionHandler) {
    this.connectionHandler = connectionHandler;
  }

  @Override
  public void run() {
    Integer queue = QueueHelper.createQueue(connection, QueueHelper.DEFAULT_CREATOR);
    if (queue != null) {
      Server.clientExecutor.execute(new ResponseHandler(
          connectionHandler, new CreateQueueResponse(queue)));
    }
  }
}
