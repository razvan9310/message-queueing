package logging;

import logging.config.PeriodicLoggerConfig;

import java.io.BufferedWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by damachir on 10/29/15.
 */
public class PeriodicLogger extends Logger {
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
          // TODO
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
