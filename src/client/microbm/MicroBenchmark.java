package client.microbm;

import client.Client;
import logging.Logger;
import message.CreateQueueRequest;
import message.CreateQueueResponse;
import message.Request;
import message.Response;
import message.SendMessageRequest;
import utils.Types;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by damachir on 11/5/15.
 */
public class MicroBenchmark {
  private Logger logger;
  private Request request;
  private Response response;

  void benchmark(Client client, int requestType, int messageLength) {
    int clientNumber = 0;

    CreateQueueRequest createQueueRequest = new CreateQueueRequest(clientNumber);
    CreateQueueResponse createQueueResponse = null;
    try {
      createQueueResponse = (CreateQueueResponse) client.sendRequest(createQueueRequest);
    } catch (IOException e) {
      System.exit(1);
    }

    int queue = createQueueResponse.getQueue();
    String logName;

    switch (requestType) {
      case Request.TYPE_SEND_TO_RECEIVER:
        request = new SendMessageRequest(0, 0, queue, Types.randomString(messageLength));
        logName = "send-to-receiver.log";
        break;
      case Request.TYPE_SEND_NO_RECEIVER:
        request = new SendMessageRequest(0, SendMessageRequest.NO_RECEIVER, queue, Types.randomString(messageLength));
        logName = "send-no-receiver.log";
        break;
    }
  }
}
