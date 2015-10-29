package logging.config;

import java.util.concurrent.TimeUnit;

/**
 * Created by damachir on 10/29/15.
 */
public class LoggerConfig {
  private long initialFlushTime;
  private long flushTimePeriod;
  private TimeUnit timeUnit;

  public LoggerConfig(long initialFlushTime, long flushTimePeriod, TimeUnit timeUnit) {
    this.initialFlushTime = initialFlushTime;
    this.flushTimePeriod = flushTimePeriod;
    this.timeUnit = timeUnit;
  }

  public LoggerConfig(LoggerConfig other) {
    this.initialFlushTime = other.initialFlushTime;
    this.flushTimePeriod = other.flushTimePeriod;
    this.timeUnit = other.timeUnit;
  }

  public long getInitialFlushTime() {
    return initialFlushTime;
  }

  public long getFlushTimePeriod() {
    return flushTimePeriod;
  }

  public TimeUnit getTimeUnit() {
    return timeUnit;
  }
}
