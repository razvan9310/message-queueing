package server;

import server.database.DatabaseThreadPoolExecutor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Server {
  private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

  public static ExecutorService clientExecutor;
  public static ExecutorService databaseExecutor;

  private ServerSocketChannel serverSocketChannel;

  public Server(ServerSocketChannel serverSocketChannel) {
    this.serverSocketChannel = serverSocketChannel;
    clientExecutor = Executors.newFixedThreadPool(10);
    databaseExecutor = new DatabaseThreadPoolExecutor(10, 10, 0, TimeUnit.NANOSECONDS,
        new LinkedBlockingQueue<Runnable>());
  }

  public void start() {
    try {
      Selector selector = SelectorProvider.provider().openSelector();
      serverSocketChannel.configureBlocking(false);
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

      while (true) {
        if (selector.select() == 0) {
          continue;
        }
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

        while (keyIterator.hasNext()) {
          SelectionKey key = keyIterator.next();
          keyIterator.remove();
          if (!key.isValid()) {
            continue;
          }

          if (key.isAcceptable()) {
            LOGGER.info("Accepting new connection.");
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
          } else if (key.isReadable()) {
            if (!(key.attachment() instanceof ConnectionHandler)) {
              ConnectionHandler connectionHandler =
                  new ConnectionHandler((SocketChannel) key.channel());
              key.attach(connectionHandler);
            }
            ConnectionHandler connectionHandler = (ConnectionHandler) key.attachment();
            byte[] requestData = connectionHandler.readFromSocketChannel();

            if (requestData != null) {
              clientExecutor.execute(new RequestHandler(
                  connectionHandler, requestData, connectionHandler.getMessageSize()));
            }
          }
        }
      }
    } catch (IOException e) {
      LOGGER.severe("Server error: " + e.getMessage());
    }

    try {
      serverSocketChannel.close();
    } catch (IOException e) {
      LOGGER.warning("ServerSocketChannel exception on shutdown: " + e.getMessage());
    }
    databaseExecutor.shutdown();
    clientExecutor.shutdown();
  }
}