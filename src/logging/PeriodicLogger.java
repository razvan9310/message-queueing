package logging;

import logging.config.PeriodicLoggerConfig;

import java.io.BufferedWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by damachir on 10/29/15.
 */
public class PeriodicLogger extends Logger {
  private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Logger.class.getName());

  private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private boolean started;

  public PeriodicLogger(PeriodicLoggerConfig config, BufferedWriter bufferedWriter) {
    super(config, bufferedWriter);
    started = false;
  }

  @Override
  public void start() {
    final PeriodicLoggerConfig periodicLoggerConfig = (PeriodicLoggerConfig) config;
    scheduler.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        try {
          log(String.valueOf(periodicLoggerConfig.getTask().call()));
        } catch (Exception e) {
          LOGGER.warning("Failed to execute periodic logging task: " + e.getMessage());
        }
      }
    },
        periodicLoggerConfig.getInitialLogTime(),
        periodicLoggerConfig.getLogTimePeriod(),
        periodicLoggerConfig.getTimeUnit());

    super.start();
    started = true;
  }

  public boolean isStarted() {
    return started;
  }
}
