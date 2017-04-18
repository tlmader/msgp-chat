package csci4311.chat;

/**
 * Defines methods for handling core functionality of maintaining users, groups, messages, etc.
 *
 * @author Ted Mader
 */
public interface MessageServer {

  /**
   * Handles a user join request.
   *
   * @param user  the name of a user
   * @param group the name of a group
   */
  MsgpMessage join(String user, String group);

  /**
   * Handles a user leave request.
   *
   * @param user the name of a user
   */
  MsgpMessage leave(String user);

  /**
   * Handles the sending of a message.
   *
   * @param user    the name of a user
   * @param message the message to send
   */
  MsgpMessage send(String user, String message);

  /**
   * Handles a request for the list of groups.
   *
   * @param user the name of a user
   */
  MsgpMessage groups(String user);

  /**
   * Handles a request for the list of users of a group.
   *
   * @param user  the name of a user
   * @param group the name of a group
   */
  MsgpMessage users(String user, String group);

  /**
   * Handles a request for the history of a group.
   *
   * @param user  the name of a user
   * @param group the name of a group
   */
  MsgpMessage history(String user, String group);
}
