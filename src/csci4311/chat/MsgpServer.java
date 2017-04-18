package csci4311.chat;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Defines methods for sending and receiving of encoded messages.
 *
 * @author Ted Mader
 */
public interface MsgpServer {

  /**
   * Encodes a user join request.
   *
   * @param exchange the HttpExchange
   */
  void join(HttpExchange exchange) throws IOException;

  /**
   * Encodes a user leave request.
   *
   * @param exchange the HttpExchange
   */
  void leave(HttpExchange exchange) throws IOException;

  /**
   * Encodes the sending of a message.
   *
   * @param exchange the HttpExchange
   */

  void send(HttpExchange exchange) throws IOException;

  /**
   * Encodes requests for the list of groups.
   *
   * @param exchange the HttpExchange
   */
  void groups(HttpExchange exchange) throws IOException;

  /**
   * Encodes requests for the list users of a group.
   *
   * @param exchange the HttpExchange
   */
  void users(HttpExchange exchange) throws IOException;

  /**
   * Encodes requests for the history of a group.
   *
   * @param exchange the HttpExchange
   */
  void history(HttpExchange exchange) throws IOException;
}
