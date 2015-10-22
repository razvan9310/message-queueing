package server;

import message.Response;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class ResponseHandler implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(ResponseHandler.class.getName());

  private ConnectionHandler connectionHandler;
  private Response response;

  public ResponseHandler(ConnectionHandler connectionHandler, Response response) {
    this.connectionHandler = connectionHandler;
    this.response = response;
  }

  @Override
  public void run() {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
      response.send(dataOutputStream);
      connectionHandler.writeToSocketChannel(byteArrayOutputStream);
    } catch (IOException e) {
      LOGGER.warning("Failed to handle response: " + e.getMessage());
    }
  }
}
