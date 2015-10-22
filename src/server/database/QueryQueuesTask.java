package server.database;

import database.QueueHelper;
import message.QueryQueuesRequest;
import message.QueryQueuesResponse;
import server.ConnectionHandler;
import server.ResponseHandler;
import server.Server;

import java.util.ArrayList;

/**
 * Created by razvan on 22.10.2015.
 */
public class QueryQueuesTask extends DatabaseRunnable {
  private QueryQueuesRequest request;
  private ConnectionHandler connectionHandler;

  public QueryQueuesTask(QueryQueuesRequest request, ConnectionHandler connectionHandler) {
    this.request = request;
    this.connectionHandler = connectionHandler;
  }

  @Override
  public void run() {
    ArrayList<Integer> queues = QueueHelper.queryQueuesForReceiver(
        connection, request.getReceiver());
    Server.clientExecutor.execute(new ResponseHandler(
        connectionHandler, new QueryQueuesResponse(queues)));
  }
}
