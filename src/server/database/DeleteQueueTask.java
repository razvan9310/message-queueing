package server.database;

import database.QueueHelper;
import message.DeleteQueueRequest;
import message.DeleteQueueResponse;
import server.ConnectionHandler;
import server.ResponseHandler;
import server.Server;

/**
 * Created by razvan on 22.10.2015.
 */
public class DeleteQueueTask extends DatabaseRunnable {
  private DeleteQueueRequest request;
  private ConnectionHandler connectionHandler;

  public DeleteQueueTask(DeleteQueueRequest request, ConnectionHandler connectionHandler) {
    request = request;
    connectionHandler = connectionHandler;
  }

  @Override
  public void run() {
    boolean deleted = QueueHelper.deleteQueue(connection, request.getQueue());
    Server.clientExecutor.execute(new ResponseHandler(
        connectionHandler, new DeleteQueueResponse(deleted)));
  }
}
