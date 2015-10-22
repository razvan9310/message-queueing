package client;

import message.CreateQueueResponse;
import message.DeleteQueueResponse;
import message.EmptyResponse;
import message.InvalidRequestResponse;
import message.MessageResponse;
import message.QueryQueuesResponse;
import message.Request;
import message.Response;
import message.SendMessageResponse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
  private Socket socket;
  private DataInputStream dataInputStream;
  private DataOutputStream dataOutputStream;

  public Client(Socket socket) throws IOException {
    this.socket = socket;
    this.dataInputStream = new DataInputStream(
        new BufferedInputStream(this.socket.getInputStream()));
    this.dataOutputStream = new DataOutputStream(
        new BufferedOutputStream(this.socket.getOutputStream()));
  }

  public Client(InetAddress inetAddress, int port) throws IOException {
    this(new Socket(inetAddress, port));
  }

  public Client(String host, int port) throws IOException {
    this(InetAddress.getByName(host), port);
  }

  public Response sendRequest(Request request) throws IOException {
    byte[] requestData = request.getBytes();
    dataOutputStream.writeInt(requestData.length);
    dataOutputStream.write(requestData);
    dataOutputStream.flush();

    byte responseType = dataInputStream.readByte();
    switch (responseType) {
      case Response.TYPE_SENT_OK:
        return new SendMessageResponse(true);
      case Response.TYPE_SENT_NOT_OK:
        return new SendMessageResponse(false);
      case Response.TYPE_EMPTY:
        return new EmptyResponse();
      case Response.TYPE_MESSAGE:
        return new MessageResponse(dataInputStream.readUTF());
      case Response.TYPE_QUEUE_CREATED:
        return new CreateQueueResponse(dataInputStream.readInt());
      case Response.TYPE_QUEUE_DELETED:
        return new DeleteQueueResponse(dataInputStream.readBoolean());
      case Response.TYPE_QUEUE_QUERY:
        ArrayList<Integer> queues = new ArrayList<Integer>();
        int size = dataInputStream.readInt();
        for (int i = 0; i < size; ++i) {
          queues.add(dataInputStream.readInt());
        }
        return new QueryQueuesResponse(queues);
      default:
        return new InvalidRequestResponse();
    }
  }
}
