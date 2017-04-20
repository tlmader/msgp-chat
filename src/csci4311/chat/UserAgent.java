package csci4311.chat;

/**
 * Deliver a (remote) message to the user agent.
 */
public interface UserAgent {
  void deliver(String message);
}
