package message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class QueryQueuesResponse extends Response {
  private List<Integer> queues;

  public QueryQueuesResponse(List<Integer> queues) {
    this.queues = queues;
  }

  public List<Integer> getQueues() {
    return queues;
  }

  @Override
  public void send(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeByte(Response.TYPE_QUEUE_QUERY);
    dataOutputStream.writeInt(queues.size());
    for (int qid : queues) {
      dataOutputStream.writeInt(qid);
    }
    dataOutputStream.flush();
  }
}
