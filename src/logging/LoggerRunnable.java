package logging;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by damachir on 10/28/15.
 */
public class LoggerRunnable implements Runnable {
  private BufferedWriter bufferedWriter;

  public LoggerRunnable(BufferedWriter bufferedWriter) {
    this.bufferedWriter = bufferedWriter;
  }

  @Override
  public void run() {
    try {
      bufferedWriter.flush();
    } catch (IOException e) {}
  }
}
