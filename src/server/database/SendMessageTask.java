package server.database;

import database.MessageHelper;
import logging.Logger;
import message.Request;
import message.SendMessageRequest;
import message.SendMessageResponse;
import server.ConnectionHandler;
import server.ResponseHandler;
import server.Server;

public class SendMessageTask extends DatabaseRunnable {
  public SendMessageTask(Request request, ConnectionHandler connectionHandler, Logger databaseResponseLogger) {
    super(request, connectionHandler, databaseResponseLogger);
  }

  @Override
  public void run() {
    double arrivalTimestamp;
    SendMessageRequest sendMessageRequest = (SendMessageRequest) request;
    if (sendMessageRequest.getReceiver() == SendMessageRequest.NO_RECEIVER) {
      arrivalTimestamp = MessageHelper.sendMessage(
          connection, sendMessageRequest.getSender(), sendMessageRequest.getQueue(), sendMessageRequest.getText(),
          databaseResponseLogger);
    } else {
      arrivalTimestamp = MessageHelper.sendMessageWithReceiver(
          connection, sendMessageRequest.getSender(), sendMessageRequest.getReceiver(), sendMessageRequest.getQueue(),
          sendMessageRequest.getText(), databaseResponseLogger);
    }
    Server.clientExecutor.execute(new ResponseHandler(
        connectionHandler, new SendMessageResponse(arrivalTimestamp)));
  }
}
