package server.database;

import database.Message;
import database.MessageHelper;
import message.SendMessageRequest;
import message.SendMessageResponse;
import server.ConnectionHandler;
import server.ResponseHandler;
import server.Server;

/**
 * Created by razvan on 22.10.2015.
 */
public class SendMessageTask extends DatabaseRunnable {
  private SendMessageRequest request;
  private ConnectionHandler connectionHandler;

  public SendMessageTask(SendMessageRequest request, ConnectionHandler connectionHandler) {
    this.request = request;
    this.connectionHandler = connectionHandler;
  }

  @Override
  public void run() {
    boolean sent;
    if (request.getReceiver() == SendMessageRequest.NO_RECEIVER) {
      sent = MessageHelper.sendMessage(
          connection, request.getSender(), request.getQueue(), request.getText());
    } else {
      sent = MessageHelper.sendMessageWithReceiver(
          connection, request.getSender(), request.getReceiver(), request.getQueue(),
          request.getText());
    }
    Server.clientExecutor.execute(new ResponseHandler(
        connectionHandler, new SendMessageResponse(sent)));
  }
}
