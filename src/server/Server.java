package server;

import logging.PeriodicTaskLogger;
import logging.config.LoggerConfig;
import logging.config.PeriodicTaskLoggerConfig;
import server.database.CountMessagesTask;
import server.database.DatabaseThreadPoolExecutor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Server {
  private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

  public static ExecutorService clientExecutor;
  public static ExecutorService databaseExecutor;

  private ServerSocketChannel serverSocketChannel;
  private int serverNumber;
  private boolean logThroughput;
  private boolean logDatabaseResponseTime;
  private boolean logMessageCount;

  public Server(ServerSocketChannel serverSocketChannel, int serverNumber, boolean logThroughput,
      boolean logDatabaseResponseTime, boolean logMessageCount) {
    this.serverSocketChannel = serverSocketChannel;
    this.serverNumber = serverNumber;
    this.logThroughput = logThroughput;
    this.logDatabaseResponseTime = logDatabaseResponseTime;
    this.logMessageCount = logMessageCount;

    clientExecutor = Executors.newFixedThreadPool(10);

    logging.Logger throughputLogger = null;
    if (logThroughput) {
      try {
        FileWriter fileWriter = new FileWriter(new File("throughput" + serverNumber + ".log"), true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        LoggerConfig config = new LoggerConfig(1, 1, TimeUnit.SECONDS);
        throughputLogger = new logging.Logger(config, bufferedWriter);
      } catch (IOException e) {
        LOGGER.warning("Failed to open throughput log: " + e.getMessage());
      }
    }
    databaseExecutor = new DatabaseThreadPoolExecutor(10, 10, 0, TimeUnit.NANOSECONDS,
        new LinkedBlockingQueue<Runnable>(), throughputLogger);
  }

  public void start() {
    try {
      Selector selector = SelectorProvider.provider().openSelector();
      serverSocketChannel.configureBlocking(false);
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

      if (logMessageCount) {
        FileWriter fileWriter = new FileWriter(new File("message-count" + serverNumber + ".log"), true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        LoggerConfig config = new LoggerConfig(1, 1, TimeUnit.SECONDS);
        Callable<String> countMessagesTask = new Callable<String>() {
          @Override
          public String call() throws Exception {
            CountMessagesTask countMessagesTask = new CountMessagesTask();
            Future result = databaseExecutor.submit(countMessagesTask);
            result.get();
            return String.valueOf(System.nanoTime() - ServerMain.startupTime) + " "
                + countMessagesTask.getMessageCount();
          }
        };
        PeriodicTaskLogger databaseMessageCountLogger = new PeriodicTaskLogger(
            new PeriodicTaskLoggerConfig(config, countMessagesTask, 1, 1), bufferedWriter);
        databaseMessageCountLogger.start();
      }

      logging.Logger databaseResponseLogger = null;
      if (logDatabaseResponseTime) {
        FileWriter fileWriter = new FileWriter(new File("db-response-time" + serverNumber + ".log"), true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        LoggerConfig config = new LoggerConfig(1, 1, TimeUnit.SECONDS);
        databaseResponseLogger = new logging.Logger(config, bufferedWriter);
        databaseResponseLogger.start();
      }

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
                  connectionHandler, requestData, connectionHandler.getMessageSize(), databaseResponseLogger));
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