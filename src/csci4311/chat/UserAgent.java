package csci4311.chat;

/**
 * Deliver a (remote) message to the user agent.
 */
public interface UserAgent {
  public void deliver(String message);
}
