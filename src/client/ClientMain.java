package client;

import message.*;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class ClientMain {
  private static final Logger LOGGER = Logger.getLogger(ClientMain.class.getName());

  /**
   *
   * @param args 0: server address; 1: server port; 2: client number; 3: total number of clients
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    int port = 0;
    int clientNumber = 0;
    int totalClients = 0;
    try {
      port = Integer.parseInt(args[1]);
      clientNumber = Integer.parseInt(args[2]);
      totalClients = Integer.parseInt(args[3]);
    } catch (NumberFormatException e) {
      System.err.println("Arguments are: server_address server_port client_number total_number_of_clients");
      System.exit(0);
    }

    Client client = new Client(args[0], port);
    CreateQueueRequest createQueueRequest = new CreateQueueRequest(clientNumber);
    CreateQueueResponse createQueueResponse = (CreateQueueResponse) client.sendRequest(createQueueRequest);
    int queue = createQueueResponse.getQueue();
    // Populate list of receivers
    ArrayList<Integer> receivers = new ArrayList<>(totalClients - 1);
    for (int i = 1; i <= totalClients; ++i) {
      if (i != clientNumber) {
        receivers.add(i);
      }
    }
    // Populate list of possible message texts - as many as the clients
    SecureRandom secureRandom = new SecureRandom();
    ArrayList<String> texts = new ArrayList<>(totalClients - 1);
    for (int i = 0; i < totalClients - 1; ++i) {
      texts.add(new BigInteger(130, secureRandom).toString(32));
    }

    while (true) {
      // Send a random message to a random receiver
      int receiverIndex = ThreadLocalRandom.current().nextInt(0, totalClients - 1);
      int receiver = receivers.get(receiverIndex);
      int messageIndex = ThreadLocalRandom.current().nextInt(0, totalClients - 1);
      String text = texts.get(messageIndex);
      // TODO: clientNumber should be a property of Client
      SendMessageResponse sendMessageResponse = (SendMessageResponse) client.sendRequest(
          new SendMessageRequest(clientNumber, receiver, queue, text));
      if (sendMessageResponse.isSentSuccessfully()) {
        System.out.println("To " + receiver + "[" +  sendMessageResponse.getArrivalTimestamp() + "]: " + text);
      } else {
        System.out.println("Failed to send message.");
      }

      // Retrieve the oldest message received
      QueryQueuesResponse queryQueuesResponse = (QueryQueuesResponse) client.sendRequest(
          new QueryQueuesRequest(clientNumber));
      List<Integer> receivedQueues = queryQueuesResponse.getQueues();
      ArrayList<MessageResponse> receivedMessages = new ArrayList<>();
      for (Integer receivedQueue : receivedQueues) {
        Response response = client.sendRequest(new PeekQueueRequest(clientNumber, receivedQueue));
        if (response instanceof MessageResponse) {
          receivedMessages.add((MessageResponse) response);
        }
      }
      MessageResponse oldest = MessageResponse.getOldestMessageResponse(receivedMessages);
      if (oldest != null) {
        System.out.println(oldest.getSender() + "[" + oldest.getTimestamp() + "]: " + oldest.getText());
      }
      try {
        Thread.sleep(900);
      } catch (InterruptedException e) {}
    }
  }
}