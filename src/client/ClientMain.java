package client;

import message.CreateQueueRequest;
import message.CreateQueueResponse;
import message.EmptyResponse;
import message.MessageResponse;
import message.PopQueueRequest;
import message.Response;
import message.SendMessageRequest;
import message.SendMessageResponse;

import java.io.IOException;

public class ClientMain {
  public static void main(String[] args) throws IOException {
    int port = Integer.parseInt(args[1]);
    Client client = new Client(args[0], port);
    int clientNumber = Integer.parseInt(args[2]);

    Response response = client.sendRequest(new CreateQueueRequest());
    int queue = ((CreateQueueResponse) response).getQueue();
    System.out.println("Created queue: " + queue);

    for (int i = 0; i < 10; ++i) {
      response = client.sendRequest(
          new SendMessageRequest(1, clientNumber == 1 ? 2 : 1, queue, "request no " + i));
      System.out.println(i + ": " + ((SendMessageResponse) response).isSentSuccessfully());
    }

//    response = client.sendRequest(new PeekQueueRequest(clientNumber, queue));
//    System.out.println("Peek: " + ((MessageResponse) response).getText());

    for (int i = 0; i < 10; ++i) {
      response = client.sendRequest(new PopQueueRequest(clientNumber, queue));
      if (response instanceof EmptyResponse) {
        System.out.println("No new messages.");
      } else {
        MessageResponse messageResponse = (MessageResponse) response;
        System.out.println("Message from " + messageResponse.getSender() + ": " + messageResponse.getText());
        System.out.println("Pop: " + ((MessageResponse) response).getText());
      }
    }
  }
}