package csci4311.chat;

import com.sun.net.httpserver.HttpExchange;

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
  void join(HttpExchange exchange);

  /**
   * Encodes a user leave request.
   *
   * @param exchange the HttpExchange
   */
  void leave(HttpExchange exchange);

  /**
   * Handles the sending of a message.
   *
   * @param exchange the HttpExchange
   */

  void send(HttpExchange exchange);

  /**
   * Handles requests for the list of groups.
   *
   * @param exchange the HttpExchange
   */
  void groups(HttpExchange exchange);

  /**
   * Requests the list of groups.
   *
   * @param exchange the HttpExchange
   */
  void users(HttpExchange exchange);

  /**
   * Requests the list of groups.
   *
   * @param exchange the HttpExchange
   */
  void history(HttpExchange exchange);
}
