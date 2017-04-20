package csci4311.chat;

import java.io.Serializable;
import java.util.Set;

/**
 * Encodes a text message.
 */
public class MsgpMessage implements Serializable {
  private String from, message;
  private Set<String> to;

  /**
   * @param from    sender
   * @param to      list of recipients
   * @param message message content
   */
  public MsgpMessage(String from, Set<String> to, String message) {
    this.from = from;
    this.to = to;
    this.message = message;
  }

  String getFrom() {
    return from;
  }

  Set<String> getTo() {
    return to;
  }

  String getMessage() {
    return message;
  }
}
