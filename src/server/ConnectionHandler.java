package server;

import utils.Types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class ConnectionHandler {
  private static final Logger LOGGER = Logger.getLogger(ConnectionHandler.class.getName());
  private static final int BUFFER_SIZE = 2048;
  private static final int EMPTY_MESSAGE_SIZE = -1;

  private SocketChannel socketChannel;
  private byte[] data;
  private int dataIndex;
  private ByteBuffer readBuffer;
  private int totalBytesRead;
  private int messageSize;

  public ConnectionHandler(SocketChannel socketChannel) {
    this.socketChannel = socketChannel;
    data = new byte[BUFFER_SIZE];
    dataIndex = 0;
    readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    totalBytesRead = 0;
    messageSize = EMPTY_MESSAGE_SIZE;
  }

  public void writeToSocketChannel(ByteArrayOutputStream byteArrayOutputStream) {
    byte[] data = byteArrayOutputStream.toByteArray();
    ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
    byteBuffer.put(data);
    byteBuffer.flip();
    try {
      socketChannel.write(byteBuffer);
    } catch (IOException e) {
      LOGGER.warning("Failed to write to socket channel: " + e.getMessage());
    }
  }

  public byte[] readFromSocketChannel() {
    int bytesRead = 0;
    try {
      while ((bytesRead = socketChannel.read(readBuffer)) > 0) {
        totalBytesRead += bytesRead;

        readBuffer.flip();
        while (readBuffer.hasRemaining()) {
          data[dataIndex] = readBuffer.get();
          dataIndex++;
        }
        readBuffer.flip();

        if (messageSize == EMPTY_MESSAGE_SIZE) {
          if (totalBytesRead < Types.INTEGER_BYTES) {
            continue;
          }
          messageSize = ByteBuffer.wrap(data, 0, Types.INTEGER_BYTES).getInt();
        }

        if (totalBytesRead == Types.INTEGER_BYTES + messageSize) {
          totalBytesRead = 0;
          readBuffer.rewind();
          return data;
        }
      }
    } catch (IOException e) {
      LOGGER.warning("Failed to read from socket channel: " + e.getMessage());
    }
    return null;
  }

  public int getMessageSize() {
    try {
      return Types.INTEGER_BYTES + messageSize;
    } finally {
      messageSize = EMPTY_MESSAGE_SIZE;
      dataIndex = 0;
    }
  }

  public void closeConnection() {
    try {
      socketChannel.close();
    } catch (IOException e) {
      // Nothing, the client is gone
    }
  }
}
