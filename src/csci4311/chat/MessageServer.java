package csci4311.chat;

import java.util.List;
import java.util.Set;

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
  int join(String user, String group);

  /**
   * Handles a user leave request.
   *
   * @param user the name of a user
   */
  int leave(String user, String group);

  /**
   * Handles the sending of a message.
   *
   * @param message the message to send
   */
  int send(MsgpMessage message);

  /**
   * Handles a request for the list of groups.
   */
  Set<String> groups();

  /**
   * Handles a request for the list of users of a group.
   */
  Set<String> users();

  /**
   * Handles a request for the history of a group.
   *
   * @param user  the name of a user
   * @param group the name of a group
   */
  List<MsgpMessage> history(String user, String group);
}
