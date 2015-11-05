package client;

import logging.config.LoggerConfig;
import message.*;
import utils.Types;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ClientMain {
  private static final Logger LOGGER = Logger.getLogger(ClientMain.class.getName());

  /**
   *
   * @param args 0: server address; 1: server port; 2: client number; 3: total number of clients; 4: message length
   *             5: log_response_time
   * @throws IOException
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    int port = 0;
    int clientNumber = 0;
    int totalClients = 0;
    int messageLength = 0;
    int logResponseTime = 0;
    try {
      port = Integer.parseInt(args[1]);
      clientNumber = Integer.parseInt(args[2]);
      totalClients = Integer.parseInt(args[3]);
      messageLength = Integer.parseInt(args[4]);
      logResponseTime = Integer.parseInt(args[5]);
    } catch (NumberFormatException e) {
      System.err.println("Arguments are: server_address server_port client_number total_number_of_clients" +
          "message_length log_response_time");
      System.exit(0);
    }

    logging.Logger responseTimeLogger = null;
    if (logResponseTime == 1) {
      FileWriter fileWriter = new FileWriter(new File("response-time" + clientNumber + ".log"), true);
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
      responseTimeLogger = new logging.Logger(
          new LoggerConfig(1, 1, TimeUnit.SECONDS), bufferedWriter, logging.Logger.TYPE_OTHER);
      responseTimeLogger.start();
    }
    long startTime = System.nanoTime();

    Client client = new Client(args[0], port);
    CreateQueueRequest createQueueRequest = new CreateQueueRequest(clientNumber);
    long createQueueRequestTime = System.nanoTime();
    CreateQueueResponse createQueueResponse = (CreateQueueResponse) client.sendRequest(createQueueRequest);
    long createQueueElapsedTime = System.nanoTime() - createQueueRequestTime;
    if (responseTimeLogger != null) {
      responseTimeLogger.log(String.valueOf(createQueueRequestTime - startTime) + " "
          + String.valueOf(createQueueElapsedTime) + "\n");
    }
    Thread.sleep(50);
    
    int queue = createQueueResponse.getQueue();
    // Populate list of receivers
    ArrayList<Integer> receivers = new ArrayList<>(totalClients - 1);
    for (int i = 0; i < totalClients; ++i) {
      if (i != clientNumber) {
        receivers.add(i);
      }
    }

    while (true) {
      // Send a random message to a random receiver
      // 20% chance of sending out a message without a receiver
      int receiverIndex = ThreadLocalRandom.current().nextInt(0, totalClients + totalClients / 4);
      int receiver = receiverIndex >= receivers.size() ? SendMessageRequest.NO_RECEIVER : receivers.get(receiverIndex);
      String text = Types.randomString(messageLength);
      // TODO: clientNumber should be a property of Client
      long sendMessageRequestTime = System.nanoTime();
      SendMessageResponse sendMessageResponse = (SendMessageResponse) client.sendRequest(
          new SendMessageRequest(clientNumber, receiver, queue, text));
      long sendMessageElapsedTime = System.nanoTime() - sendMessageRequestTime;
      if (responseTimeLogger != null) {
        responseTimeLogger.log(String.valueOf(sendMessageRequestTime - startTime) + " "
            + String.valueOf(sendMessageElapsedTime) + "\n");
      }
      Thread.sleep(10);

      if (sendMessageResponse.isSentSuccessfully()) {
        System.out.println("[" + sendMessageResponse.getArrivalTimestamp() + "]To " + receiver + ": " + text);
      } else {
        System.out.println("Failed to send message.");
      }
      System.out.println();

      // Retrieve the oldest message addressed to this client
      long queryQueuesRequestTime = System.nanoTime();
      QueryQueuesResponse queryQueuesResponse = (QueryQueuesResponse) client.sendRequest(
          new QueryQueuesRequest(clientNumber));
      long queryQueuesElapsedTime = System.nanoTime() - queryQueuesRequestTime;
      if (responseTimeLogger != null) {
        responseTimeLogger.log(String.valueOf(queryQueuesRequestTime - startTime) + " "
            + String.valueOf(queryQueuesElapsedTime) + "\n");
      }
      Thread.sleep(10);

      List<Integer> receivedQueues = queryQueuesResponse.getQueues();
      ArrayList<MessageResponse> receivedMessages = new ArrayList<>();
      for (Integer receivedQueue : receivedQueues) {
        long peekQueueRequestTime = System.nanoTime();
        Response response = client.sendRequest(new PeekQueueRequest(clientNumber, receivedQueue));
        long peekQueueElapsedTime = System.nanoTime() - peekQueueRequestTime;
        if (responseTimeLogger != null) {
          responseTimeLogger.log(String.valueOf(peekQueueRequestTime - startTime) + " "
              + String.valueOf(peekQueueElapsedTime) + "\n");
        }
        if (response instanceof MessageResponse) {
          receivedMessages.add((MessageResponse) response);
        }
        Thread.sleep(10);
      }
      
      MessageResponse oldest = MessageResponse.getOldestMessageResponse(receivedMessages);

      if (oldest != null) {
        System.out.println("[" + oldest.getTimestamp() + "]From " + oldest.getSender() + ": " + oldest.getText());
        System.out.println();
        long popQueueRequestTime = System.nanoTime();
        client.sendRequest(new PopQueueRequest(clientNumber, oldest.getQueue()));
        long popQueueElapsedTime = System.nanoTime() - popQueueRequestTime;
        if (responseTimeLogger != null) {
          responseTimeLogger.log(String.valueOf(popQueueRequestTime - startTime) + " "
              + String.valueOf(popQueueElapsedTime) + "\n");
        }
        Thread.sleep(10);
      }
    }
  }
}