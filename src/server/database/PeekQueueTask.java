package server.database;

import database.Message;
import database.MessageHelper;
import message.EmptyResponse;
import message.MessageResponse;
import message.PeekQueueRequest;
import server.ConnectionHandler;
import server.ResponseHandler;
import server.Server;

public class PeekQueueTask extends DatabaseRunnable {
  private PeekQueueRequest request;
  private ConnectionHandler connectionHandler;

  public PeekQueueTask(PeekQueueRequest request, ConnectionHandler connectionHandler) {
    this.request = request;
    this.connectionHandler = connectionHandler;
  }

  @Override
  public void run() {
    Message message = MessageHelper.peekMessage(
        connection, request.getQueue(), request.getReceiver());
    Server.clientExecutor.execute(new ResponseHandler(
            connectionHandler,
            message == null ? new EmptyResponse() : new MessageResponse(message.getText())));
  }
}