package client;

import message.CreateQueueRequest;
import message.CreateQueueResponse;
import message.MessageResponse;
import message.PeekQueueRequest;
import message.PopQueueRequest;
import message.Response;
import message.SendMessageRequest;
import message.SendMessageResponse;

import java.io.IOException;

/**
 * Created by razvan on 22.10.2015.
 */
public class ClientMain {
  public static void main(String[] args) throws IOException {
    int port = Integer.parseInt(args[0]);
    Client client = new Client("localhost", port);

    Response response = client.sendRequest(new CreateQueueRequest());
    int queue = ((CreateQueueResponse) response).getQueue();
    System.out.println("Created queue: " + queue);

    for (int i = 0; i < 10; ++i) {
      response = (SendMessageResponse) client.sendRequest(
          new SendMessageRequest(1, 1, queue, "request no " + i));
      System.out.println(i + ": " + ((SendMessageResponse) response).isSentSuccessfully());
    }

    response = (MessageResponse) client.sendRequest(new PeekQueueRequest(1, queue));
    System.out.println("Peek: " + ((MessageResponse) response).getText());

    for (int i = 0; i < 10; ++i) {
      response = (MessageResponse) client.sendRequest(new PopQueueRequest(1, queue));
      System.out.println("Pop: " + ((MessageResponse) response).getText());
    }
  }
}