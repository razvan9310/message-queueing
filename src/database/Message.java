package database;

public class Message {
  private int id;
  private int sender;
  private String text;
  private long timestamp;

  public Message(int id, int sender, String text, long timestamp) {
    this.id = id;
    this.sender = sender;
    this.text = text;
    this.timestamp = timestamp;
  }

  @Override
  public String toString() {
    return String.format("Message from %d: %s", sender, text);
  }

  public int getId() {
    return id;
  }

  public int getSender() {
    return sender;
  }

  public String getText() {
    return text;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
