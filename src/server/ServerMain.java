package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.logging.Logger;

public class ServerMain {
  private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

  /**
   * @param args 0: port; 1: server_number; 2: log_db_message_count
   */
  public static void main(String args[]) {
    int port = Integer.parseInt(args[0]);
    int serverNumber = Integer.parseInt(args[1]);
    int logMessageCount = Integer.parseInt(args[2]);
    try {
      ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
      serverSocketChannel.socket().bind(new InetSocketAddress(port));
      Server server = new Server(serverSocketChannel, serverNumber, logMessageCount == 1);
      LOGGER.info("Starting server");
      server.start();
    } catch (IOException e) {
      LOGGER.severe(e.getMessage());
    }
  }
}
