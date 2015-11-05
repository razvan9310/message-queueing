package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.logging.Logger;

public class ServerMain {
  private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());
  public static long startupTime;

  /**
   * @param args 0: port; 1: server_number; 2: log_throughput; 3: log_db_response_time; 4: log_db_throughput;
   *             5: log_db_message_count; 6: db_threads
   */
  public static void main(String args[]) {
    startupTime = System.nanoTime();
    int port = Integer.parseInt(args[0]);
    int serverNumber = Integer.parseInt(args[1]);
    int logThroughput = Integer.parseInt(args[2]);
    int logDbResponseTime = Integer.parseInt(args[3]);
    int logDbThroughput = Integer.parseInt(args[4]);
    int logMessageCount = Integer.parseInt(args[5]);
    int dbThreads = Integer.parseInt(args[6]);
    try {
      ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
      serverSocketChannel.socket().bind(new InetSocketAddress(port));
      Server server = new Server(
          serverSocketChannel, serverNumber, logThroughput == 1, logDbResponseTime == 1, logDbThroughput == 1,
              logMessageCount == 1, dbThreads);
      LOGGER.info("Starting server");
      server.start();
    } catch (IOException e) {
      LOGGER.severe(e.getMessage());
    }
  }
}
