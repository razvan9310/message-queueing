package logging;

import logging.config.LoggerConfig;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by damachir on 10/28/15.
 */
public class Logger {
  private BufferedWriter bufferedWriter;
  private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  protected LoggerConfig config;

  public Logger(LoggerConfig config, BufferedWriter bufferedWriter) {
    this.config = config;
    this.bufferedWriter = bufferedWriter;
  }

  public void start() {
    scheduler.scheduleAtFixedRate(
        new LoggerRunnable(bufferedWriter),
        config.getInitialFlushTime(),
        config.getFlushTimePeriod(),
        config.getTimeUnit());
  }

  public void log(String message) {
    try {
      bufferedWriter.write(message);
    } catch (IOException e) {
      // TODO
    }
  }
}
