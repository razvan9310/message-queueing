package database;

public class Message {
  private int id;
  private int sender;
  private int queue;
  private String text;
  private double timestamp;

  public Message(int id, int sender, int queue, String text, double timestamp) {
    this.id = id;
    this.sender = sender;
    this.queue = queue;
    this.text = text;
    this.timestamp = timestamp;
  }

  public int getId() {
    return id;
  }

  public int getSender() {
    return sender;
  }

  public int getQueue() {
    return queue;
  }

  public String getText() {
    return text;
  }

  public double getTimestamp() {
    return timestamp;
  }

  public void setSender(int sender) {
    this.sender = sender;
  }

  public void setQueue(int queue) {
    this.queue = queue;
  }
}
