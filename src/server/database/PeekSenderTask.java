package server.database;

import database.Message;
import database.MessageHelper;
import message.EmptyResponse;
import message.MessageResponse;
import message.PeekSenderRequest;
import server.ConnectionHandler;
import server.ResponseHandler;
import server.Server;

/**
 * Created by razvan on 22.10.2015.
 */
public class PeekSenderTask extends DatabaseRunnable {
  private PeekSenderRequest request;
  private ConnectionHandler connectionHandler;

  public PeekSenderTask(PeekSenderRequest request, ConnectionHandler connectionHandler) {
    this.request = request;
    this.connectionHandler = connectionHandler;
  }

  @Override
  public void run() {
    Message message = MessageHelper.peekMessageFromSender(
        connection, request.getSender(), request.getReceiver());
    Server.clientExecutor.execute(new ResponseHandler(
            connectionHandler,
            message == null ? new EmptyResponse() : new MessageResponse(message.getText())));
  }
}
