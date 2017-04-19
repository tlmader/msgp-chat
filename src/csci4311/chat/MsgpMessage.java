package csci4311.chat;

import java.io.Serializable;
import java.util.List;

/**
 * Encodes a text message.
 */
public class MsgpMessage implements Serializable {
  private String from, message;
  private List<String> to;

  /**
   * @param from    sender
   * @param to      list of recipients
   * @param message message content
   */
  public MsgpMessage(String from, List<String> to, String message) {
    this.from = from;
    this.to = to;
    this.message = message;
  }

  public String getFrom() {
    return from;
  }

  public List<String> getTo() {
    return to;
  }

  public String getMessage() {
    return message;
  }
}
