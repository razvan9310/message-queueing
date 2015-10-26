package server.database;

import database.Message;
import database.MessageHelper;
import message.EmptyResponse;
import message.MessageResponse;
import message.PopQueueRequest;
import server.ConnectionHandler;
import server.ResponseHandler;
import server.Server;

public class PopQueueTask extends DatabaseRunnable {
  private PopQueueRequest request;
  private ConnectionHandler connectionHandler;

  public PopQueueTask(PopQueueRequest request, ConnectionHandler connectionHandler) {
    this.request = request;
    this.connectionHandler = connectionHandler;
  }

  @Override
  public void run() {
    Message message = MessageHelper.popMessage(
        connection, request.getQueue(), request.getReceiver());
    Server.clientExecutor.execute(new ResponseHandler(
        connectionHandler,
        message == null ? new EmptyResponse() : new MessageResponse(
            message.getSender(), message.getText(), message.getTimestamp())));
  }
}
