package client;

import database.QueueHelper;
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
  public static void main(String[] args) {
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
      FileWriter fileWriter;
      try {
        fileWriter = new FileWriter(new File("response-time" + clientNumber + ".log"), true);
      } catch (IOException e) {
        LOGGER.severe("Failed to open logging FileWriter: " + e.getMessage());
        return;
      }
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
      responseTimeLogger = new logging.Logger(
          new LoggerConfig(1, 1, TimeUnit.SECONDS), bufferedWriter, logging.Logger.TYPE_OTHER);
      responseTimeLogger.start();
    }
    long startTime = System.nanoTime();

    Client client;
    try {
      client = new Client(args[0], port);
    } catch (IOException e) {
      LOGGER.severe("Failed to create client: " + e.getMessage());
      return;
    }
    CreateQueueRequest createQueueRequest = new CreateQueueRequest(clientNumber);
    CreateQueueResponse createQueueResponse;
    long createQueueRequestTime = System.nanoTime();
    try {
       createQueueResponse = (CreateQueueResponse) client.sendRequest(createQueueRequest);
    } catch (IOException e) {
      LOGGER.severe("Failed to create queue: " + e.getMessage());
      return;
    }
    long createQueueElapsedTime = System.nanoTime() - createQueueRequestTime;
    if (responseTimeLogger != null) {
      responseTimeLogger.log(String.valueOf(createQueueRequestTime - startTime) + " "
          + String.valueOf(createQueueElapsedTime) + "\n");
    }
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {}
    
    int queue = createQueueResponse.getQueue();
    if (queue == QueueHelper.NO_QUEUE) {
      LOGGER.severe("Failed to create queue: database failure.");
      return;
    }
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
      SendMessageResponse sendMessageResponse = null;
      long sendMessageRequestTime = System.nanoTime();
      try {
        sendMessageResponse = (SendMessageResponse) client.sendRequest(
            new SendMessageRequest(clientNumber, receiver, queue, text));
        long sendMessageElapsedTime = System.nanoTime() - sendMessageRequestTime;
        if (responseTimeLogger != null) {
          responseTimeLogger.log(String.valueOf(sendMessageRequestTime - startTime) + " "
              + String.valueOf(sendMessageElapsedTime) + "\n");
        }
      } catch (IOException e) {
        LOGGER.warning("Failed to send message: " + e.getMessage());
      }
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {}

      if (sendMessageResponse != null && sendMessageResponse.isSentSuccessfully()) {
        System.out.println("[" + sendMessageResponse.getArrivalTimestamp() + "]To " + receiver + ": " + text);
      } else {
        System.out.println("Failed to send message.");
      }
      System.out.println();

      // Retrieve the oldest message addressed to this client
      QueryQueuesResponse queryQueuesResponse = null;
      long queryQueuesRequestTime = System.nanoTime();
      try {
        queryQueuesResponse = (QueryQueuesResponse) client.sendRequest(
            new QueryQueuesRequest(clientNumber));
        long queryQueuesElapsedTime = System.nanoTime() - queryQueuesRequestTime;
        if (responseTimeLogger != null) {
          responseTimeLogger.log(String.valueOf(queryQueuesRequestTime - startTime) + " "
              + String.valueOf(queryQueuesElapsedTime) + "\n");
        }
      } catch (IOException e) {
        LOGGER.warning("Failed to query queues for client: " + e.getMessage());
      }
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {}

      MessageResponse oldest = null;
      if (queryQueuesResponse != null) {
        List<Integer> receivedQueues = queryQueuesResponse.getQueues();
        ArrayList<MessageResponse> receivedMessages = new ArrayList<>();
        for (Integer receivedQueue : receivedQueues) {
          Response response = null;
          long peekQueueRequestTime = System.nanoTime();
          try {
            response = client.sendRequest(new PeekQueueRequest(clientNumber, receivedQueue));
            long peekQueueElapsedTime = System.nanoTime() - peekQueueRequestTime;
            if (responseTimeLogger != null) {
              responseTimeLogger.log(String.valueOf(peekQueueRequestTime - startTime) + " "
                  + String.valueOf(peekQueueElapsedTime) + "\n");
            }
          } catch (IOException e) {
            LOGGER.warning("Failed to peek queue: " + e.getMessage());
          }
          if (response instanceof MessageResponse) {
            receivedMessages.add((MessageResponse) response);
          }
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {}
        }
        oldest = MessageResponse.getOldestMessageResponse(receivedMessages);
      }

      if (oldest != null) {
        System.out.println("[" + oldest.getTimestamp() + "]From " + oldest.getSender() + ": " + oldest.getText());
        System.out.println();
        long popQueueRequestTime = System.nanoTime();
        try {
          client.sendRequest(new PopQueueRequest(clientNumber, oldest.getQueue()));
          long popQueueElapsedTime = System.nanoTime() - popQueueRequestTime;
          if (responseTimeLogger != null) {
            responseTimeLogger.log(String.valueOf(popQueueRequestTime - startTime) + " "
                + String.valueOf(popQueueElapsedTime) + "\n");
          }
        } catch (IOException e) {
          LOGGER.warning("Failed to pop queue: " + e.getMessage());
        }
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {}
      }
    }
  }
}