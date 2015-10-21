package database;

/**
 * Created by razvan on 21.10.2015.
 */
public class Message {
  private int id;
  private int sender;
  private String text;

  public Message(int id, int sender, String text) {
    this.id = id;
    this.sender = sender;
    this.text = text;
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
}
