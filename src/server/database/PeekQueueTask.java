package server.database;

import database.Message;
import database.MessageHelper;
import logging.Logger;
import message.EmptyResponse;
import message.MessageResponse;
import message.PeekQueueRequest;
import message.Request;
import server.ConnectionHandler;
import server.ResponseHandler;
import server.Server;

public class PeekQueueTask extends DatabaseRunnable {
  public PeekQueueTask(Request request, ConnectionHandler connectionHandler, Logger databaseResponseLogger) {
    super(request, connectionHandler, databaseResponseLogger);
  }

  @Override
  public void run() {
    Message message = MessageHelper.peekQueue(
        connection, ((PeekQueueRequest) request).getQueue(), ((PeekQueueRequest) request).getReceiver(),
        databaseResponseLogger);
    Server.clientExecutor.execute(new ResponseHandler(
        connectionHandler,
        message == null ? new EmptyResponse() : new MessageResponse(
            message.getSender(), message.getQueue(), message.getText(), message.getTimestamp())));
  }
}