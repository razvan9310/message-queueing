package server.database;

import database.QueueHelper;
import logging.Logger;
import message.QueryQueuesRequest;
import message.QueryQueuesResponse;
import message.Request;
import server.ConnectionHandler;
import server.ResponseHandler;
import server.Server;

import java.util.ArrayList;

public class QueryQueuesTask extends DatabaseRunnable {
  public QueryQueuesTask(Request request, ConnectionHandler connectionHandler, Logger databaseResponseLogger) {
    super(request, connectionHandler, databaseResponseLogger);
  }

  @Override
  public void run() {
    ArrayList<Integer> queues = QueueHelper.queryQueuesForReceiver(
        connection, ((QueryQueuesRequest) request).getReceiver(), databaseResponseLogger);
    Server.clientExecutor.execute(new ResponseHandler(
        connectionHandler, new QueryQueuesResponse(queues)));
  }
}
