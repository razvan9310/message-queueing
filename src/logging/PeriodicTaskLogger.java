package logging;

import logging.config.PeriodicTaskLoggerConfig;

import java.io.BufferedWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by damachir on 10/29/15.
 */
public class PeriodicTaskLogger extends Logger {
  private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Logger.class.getName());

  private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  public PeriodicTaskLogger(PeriodicTaskLoggerConfig config, BufferedWriter bufferedWriter) {
    super(config, bufferedWriter);
  }

  @Override
  public void start() {
    final PeriodicTaskLoggerConfig periodicTaskLoggerConfig = (PeriodicTaskLoggerConfig) config;
    scheduler.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        try {
          log(String.valueOf(periodicTaskLoggerConfig.getTask().call()) + "\n");
        } catch (Exception e) {
          LOGGER.warning("Failed to execute periodic logging task: " + e.getMessage());
        }
      }
    },
        periodicTaskLoggerConfig.getInitialLogTime(),
        periodicTaskLoggerConfig.getLogTimePeriod(),
        periodicTaskLoggerConfig.getTimeUnit());

    super.start();
  }
}
