package logging.config;

import java.util.concurrent.Callable;

/**
 * Created by damachir on 10/29/15.
 */
public class PeriodicTaskLoggerConfig extends LoggerConfig {
  private Callable task;
  private long initialLogTime;
  private long logTimePeriod;

  public PeriodicTaskLoggerConfig(LoggerConfig loggerConfig, Callable task, long initialLogTime, long logTimePeriod) {
    super(loggerConfig);
    this.task = task;
    this.initialLogTime = initialLogTime;
    this.logTimePeriod = logTimePeriod;
  }

  public Callable getTask() {
    return task;
  }

  public long getInitialLogTime() {
    return initialLogTime;
  }

  public long getLogTimePeriod() {
    return logTimePeriod;
  }
}
