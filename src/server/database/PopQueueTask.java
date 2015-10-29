package server.database;

import database.Message;
import database.MessageHelper;
import logging.Logger;
import message.EmptyResponse;
import message.MessageResponse;
import message.PopQueueRequest;
import message.Request;
import server.ConnectionHandler;
import server.ResponseHandler;
import server.Server;

public class PopQueueTask extends DatabaseRunnable {
  public PopQueueTask(Request request, ConnectionHandler connectionHandler, Logger databaseResponseLogger) {
    super(request, connectionHandler, databaseResponseLogger);
  }

  @Override
  public void run() {
    Message message = MessageHelper.popMessage(
        connection, ((PopQueueRequest) request).getQueue(), ((PopQueueRequest) request).getReceiver(),
        databaseResponseLogger);
    Server.clientExecutor.execute(new ResponseHandler(
        connectionHandler,
        message == null ? new EmptyResponse() : new MessageResponse(
            message.getSender(), message.getQueue(), message.getText(), message.getTimestamp())));
  }
}
