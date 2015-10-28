package client;

import message.*;
import utils.Types;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ClientMain {
  private static final Logger LOGGER = Logger.getLogger(ClientMain.class.getName());
  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  private static class LoggerRunnable implements Runnable {
    private BufferedWriter bufferedWriter;

    public LoggerRunnable(BufferedWriter bufferedWriter) {
      this.bufferedWriter = bufferedWriter;
    }

    @Override
    public void run() {
      while (true) {
        try {
          bufferedWriter.flush();
        } catch (IOException e) {}
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {}
      }
    }
  }

  /**
   *
   * @param args 0: server address; 1: server port; 2: client number; 3: total number of clients; 4: message length
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    int port = 0;
    int clientNumber = 0;
    int totalClients = 0;
    int messageLength = 0;
    try {
      port = Integer.parseInt(args[1]);
      clientNumber = Integer.parseInt(args[2]);
      totalClients = Integer.parseInt(args[3]);
      messageLength = Integer.parseInt(args[4]);
    } catch (NumberFormatException e) {
      System.err.println(
          "Arguments are: server_address server_port client_number total_number_of_clients message_length");
      System.exit(0);
    }
    FileWriter fileWriter = new FileWriter(new File("requests" + clientNumber + ".log"), true);
    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
    scheduler.scheduleAtFixedRate(new LoggerRunnable(bufferedWriter), 1, 1, TimeUnit.SECONDS);
    long startTime = System.nanoTime();

    Client client = new Client(args[0], port);
    CreateQueueRequest createQueueRequest = new CreateQueueRequest(clientNumber);
    long createQueueRequestTime = System.nanoTime();
    CreateQueueResponse createQueueResponse = (CreateQueueResponse) client.sendRequest(createQueueRequest);
    long createQueueElapsedTime = System.nanoTime() - createQueueRequestTime;
    bufferedWriter.write(String.valueOf(createQueueRequestTime - startTime) + " "
        + String.valueOf(createQueueElapsedTime) + "\n");
    int queue = createQueueResponse.getQueue();
    // Populate list of receivers
    ArrayList<Integer> receivers = new ArrayList<>(totalClients - 1);
    for (int i = 1; i <= totalClients; ++i) {
      if (i != clientNumber) {
        receivers.add(i);
      }
    }

    while (true) {
      // Send a random message to a random receiver
      int receiverIndex = ThreadLocalRandom.current().nextInt(0, totalClients - 1);
      int receiver = receivers.get(receiverIndex);
      String text = Types.randomString(messageLength);
      // TODO: clientNumber should be a property of Client
      long sendMessageRequestTime = System.nanoTime();
      SendMessageResponse sendMessageResponse = (SendMessageResponse) client.sendRequest(
          new SendMessageRequest(clientNumber, receiver, queue, text));
      long sendMessageElapsedTime = System.nanoTime() - sendMessageRequestTime;
      bufferedWriter.write(String.valueOf(sendMessageRequestTime - startTime) + " "
          + String.valueOf(sendMessageElapsedTime) + "\n");

      if (sendMessageResponse.isSentSuccessfully()) {
        System.out.println("[" + sendMessageResponse.getArrivalTimestamp() + "]To " + receiver + ": " + text);
      } else {
        System.out.println("Failed to send message.");
      }
      System.out.println();

      // Retrieve the oldest message received
      long queryQueuesRequestTime = System.nanoTime();
      QueryQueuesResponse queryQueuesResponse = (QueryQueuesResponse) client.sendRequest(
          new QueryQueuesRequest(clientNumber));
      long queryQueuesElapsedTime = System.nanoTime() - queryQueuesRequestTime;
      bufferedWriter.write(String.valueOf(queryQueuesRequestTime - startTime) + " "
          + String.valueOf(queryQueuesElapsedTime) + "\n");

      List<Integer> receivedQueues = queryQueuesResponse.getQueues();
      ArrayList<MessageResponse> receivedMessages = new ArrayList<>();
      for (Integer receivedQueue : receivedQueues) {
        long peekQueueRequestTime = System.nanoTime();
        Response response = client.sendRequest(new PeekQueueRequest(clientNumber, receivedQueue));
        long peekQueueElapsedTime = System.nanoTime() - peekQueueRequestTime;
        bufferedWriter.write(String.valueOf(peekQueueRequestTime - startTime) + " "
            + String.valueOf(peekQueueElapsedTime) + "\n");

        if (response instanceof MessageResponse) {
          receivedMessages.add((MessageResponse) response);
        }
      }
      MessageResponse oldest = MessageResponse.getOldestMessageResponse(receivedMessages);
      if (oldest != null) {
        System.out.println("[" + oldest.getTimestamp() + "]From " + oldest.getSender() + ": " + oldest.getText());
        System.out.println();
        long popQueueRequestTime = System.nanoTime();
        client.sendRequest(new PopQueueRequest(clientNumber, oldest.getQueue()));
        long popQueueElapsedTime = System.nanoTime() - popQueueRequestTime;
        bufferedWriter.write(String.valueOf(popQueueRequestTime - startTime) + " "
            + String.valueOf(popQueueElapsedTime) + "\n");
      }
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {}
    }
  }
}