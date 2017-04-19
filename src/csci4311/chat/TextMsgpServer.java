package csci4311.chat;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.util.HashMap;
import java.util.Set;

/**
 * Implements methods for sending and receiving of encoded messages.
 *
 * @author Ted Mader
 */
@SuppressWarnings("unchecked")
public class  TextMsgpServer implements MsgpServer {

  private ChatServer server = new ChatServer();

  @Override
  public void join(HttpExchange exchange) throws IOException {
    HashMap<String, String> list = getMapFromBody(exchange);
    handle(exchange, list != null ? server.join(list.get("user"), list.get("group")) : 400);
  }

  @Override
  public void leave(HttpExchange exchange) throws IOException {
    HashMap<String, String> list = getMapFromBody(exchange);
    handle(exchange, list != null ? server.leave(list.get("user"), list.get("group")) : 400);
  }

  @Override
  public void send(HttpExchange exchange) throws IOException {
    handle(exchange, server.send(getMessageFromBody(exchange)));
  }

  @Override
  public void groups(HttpExchange exchange) throws IOException {
    handle(exchange, server.groups());
  }

  @Override
  public void users(HttpExchange exchange) throws IOException {
    handle(exchange, server.users(getStringFromBody(exchange)));
  }

  @Override
  public void history(HttpExchange exchange) throws IOException {
    handle(exchange, server.history(getStringFromBody(exchange)));
  }

  private void handle(HttpExchange exchange, int code) throws IOException {
    exchange.sendResponseHeaders(code, 0);
  }

  private void handle(HttpExchange exchange, Set<String> set) throws IOException {
    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set("Content-Type", "text/plain");
    int code = set.isEmpty() ? 201 : 200;
    exchange.sendResponseHeaders(set.isEmpty() ? 201 : 200, 0);

    PrintStream response = new PrintStream(exchange.getResponseBody());
    response.print("msgp ");
    switch (code) {
      case 200:
        response.println("200 OK");
        break;
      case 201:
        response.println("201 No result");
        break;
      default:
        response.println("400 Error");
        break;
    }
    for (String s : set) {
      response.println(s);
    }
    response.close();
  }

  private String getStringFromBody(HttpExchange exchange) throws IOException {
    ObjectInputStream in = new ObjectInputStream(exchange.getRequestBody());
    try {
      return (String) in.readObject();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  private HashMap<String, String> getMapFromBody(HttpExchange exchange) throws IOException {
    ObjectInputStream in = new ObjectInputStream(exchange.getRequestBody());
    try {
      return (HashMap<String, String>) in.readObject();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  private MsgpMessage getMessageFromBody(HttpExchange exchange) throws IOException {
    ObjectInputStream in = new ObjectInputStream(exchange.getRequestBody());
    try {
      return (MsgpMessage) in.readObject();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}
