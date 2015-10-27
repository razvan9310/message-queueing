package client;

import message.*;

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
        } catch (IOException e) {

        }
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {

        }
      }
    }
  }

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
    FileWriter fileWriter = new FileWriter(new File("requests" + clientNumber + ".log"), true);
    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
    scheduler.scheduleAtFixedRate(new LoggerRunnable(bufferedWriter), 1, 1, TimeUnit.SECONDS);

    Client client = new Client(args[0], port);
    CreateQueueRequest createQueueRequest = new CreateQueueRequest(clientNumber);
    long createQueueRequestTime = System.nanoTime();
    CreateQueueResponse createQueueResponse = (CreateQueueResponse) client.sendRequest(createQueueRequest);
    long createQueueResponseTime = System.nanoTime();
    bufferedWriter.write(String.valueOf(createQueueRequestTime) + " " + String.valueOf(createQueueResponseTime) + "\n");
    int queue = createQueueResponse.getQueue();
    // Populate list of receivers
    ArrayList<Integer> receivers = new ArrayList<>(totalClients - 1);
    for (int i = 1; i <= totalClients; ++i) {
      if (i != clientNumber) {
        receivers.add(i);
      }
    }
    // Populate list of possible message texts - as many as the clients
    String[] texts = {
        "Such a beautiful day, isn't it?",
        "I HATE EVERYONE!!",
        "Patience is a virtue, but nobody said I'm virtuous...",
        "This sentence is not very meaningful",
        "I want to learn to play the piano!",
        "Ces mots sont en francais",
        "I would also add a sentence in German, but all I can say is 'Keine Deutsch'",
        "It's not over till it's over (duh).",
        "I am running out of random gibberish :(",
        "You are my favourite message passing client, I love you!"};

    while (true) {
      // Send a random message to a random receiver
      int receiverIndex = ThreadLocalRandom.current().nextInt(0, totalClients - 1);
      int receiver = receivers.get(receiverIndex);
      int messageIndex = ThreadLocalRandom.current().nextInt(0, texts.length);
      String text = texts[messageIndex];
      // TODO: clientNumber should be a property of Client
      long sendMessageRequestTime = System.nanoTime();
      SendMessageResponse sendMessageResponse = (SendMessageResponse) client.sendRequest(
          new SendMessageRequest(clientNumber, receiver, queue, text));
      long sendMessageResponseTime = System.nanoTime();
      bufferedWriter.write(String.valueOf(sendMessageRequestTime) + " " + String.valueOf(sendMessageResponseTime)
          + "\n");

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
      long queryQueuesResponseTime = System.nanoTime();
      bufferedWriter.write(String.valueOf(queryQueuesRequestTime) + " " + String.valueOf(queryQueuesResponseTime)
          + "\n");

      List<Integer> receivedQueues = queryQueuesResponse.getQueues();
      ArrayList<MessageResponse> receivedMessages = new ArrayList<>();
      for (Integer receivedQueue : receivedQueues) {
        long peekQueueRequestTime = System.nanoTime();
        Response response = client.sendRequest(new PeekQueueRequest(clientNumber, receivedQueue));
        long peekQueueResponseTime = System.nanoTime();
        bufferedWriter.write(String.valueOf(peekQueueRequestTime) + " " + String.valueOf(peekQueueResponseTime) + "\n");

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
        long popQueueResponseTime = System.nanoTime();
        bufferedWriter.write(String.valueOf(popQueueRequestTime) + " " + String.valueOf(popQueueResponseTime) + "\n");
      }
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {}
    }
  }
}