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
    handle(exchange, "history");
  }

  private void handle(HttpExchange exchange, int code) throws IOException {
    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set("Content-Type", "text/plain");
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

  private void handle(HttpExchange exchange, String context) throws IOException {
    String requestMethod = exchange.getRequestMethod();

    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set("Content-Type", "text/plain");
    exchange.sendResponseHeaders(200, 0);

    PrintStream response = new PrintStream(exchange.getResponseBody());
    response.println("context: /" + context + "; method: " + requestMethod
        + " uri: " + exchange.getRequestURI() + " path: " +
        exchange.getHttpContext().getPath());
    printHeaders(exchange, response);
    if (requestMethod.equalsIgnoreCase("POST")) {
      response.println("=== body ===");
      printBody(exchange, response);
    }
    response.close();
  }

  private void printHeaders(HttpExchange exchange, PrintStream response) {
    Headers requestHeaders = exchange.getRequestHeaders();
    Set<String> keySet = requestHeaders.keySet();
    for (String key : keySet) {
      response.println(key + " = " + requestHeaders.get(key));
    }
  }

  private void printBody(HttpExchange exchange, PrintStream response) throws IOException {
    BufferedReader body = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
    String bodyLine;
    while ((bodyLine = body.readLine()) != null) {
      response.println(bodyLine);
    }
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
