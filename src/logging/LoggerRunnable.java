package logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by damachir on 10/28/15.
 */
public class LoggerRunnable implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(LoggerRunnable.class.getName());
  private BufferedWriter bufferedWriter;

  public LoggerRunnable(BufferedWriter bufferedWriter) {
    this.bufferedWriter = bufferedWriter;
  }

  @Override
  public void run() {
    try {
      bufferedWriter.flush();
    } catch (IOException e) {
      LOGGER.warning("Failed to flush log buffer: " + e.getMessage());
    }
  }
}
