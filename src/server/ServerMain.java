package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.logging.Logger;

/**
 * Created by razvan on 22.10.2015.
 */
public class ServerMain {
  private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

  public static void main(String args[]) {
    int port = Integer.parseInt(args[0]);
    try {
      ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
      serverSocketChannel.socket().bind(new InetSocketAddress(port));
      Server server = new Server(serverSocketChannel);
      LOGGER.info("Starting server");
      server.start();
    } catch (IOException e) {
      LOGGER.severe(e.getMessage());
    }
  }
}

//public class ServerMain {
//  private final static Logger LOGGER = Logger.getLogger(ServerMain.class
//          .getName());
//
//  public static int SERVER_PORT = 9999;
//
//  public static void main(String args[]) {
//    try {
//      LogManager.getLogManager().readConfiguration(new FileInputStream("mylogging.properties"));
//
//      ServerSocketChannel ssc = ServerSocketChannel.open();
//
//      ssc.socket().bind(new InetSocketAddress(9999));
//      Server server = new Server(ssc, new ClientConnectionFactory());
//      LOGGER.info("Started server");
//      server.run();
//    } catch (IOException e) {
//      LOGGER.log(Level.SEVERE, "Unexpected excetion in main", e);
//    }
//  }
//}