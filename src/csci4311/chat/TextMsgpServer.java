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
public class  TextMsgpServer implements MsgpServer {

  ChatServer server = new ChatServer();

  @Override
  public void join(HttpExchange exchange) throws IOException {
    HashMap<String, String> list = getMapFromBody(exchange);
    handle(exchange, "join", server.join(list.get("user"), list.get("group")));
  }

  @Override
  public void leave(HttpExchange exchange) throws IOException {
    HashMap<String, String> list = getMapFromBody(exchange);
    server.leave(list.get("user"), list.get("group"));
    handle(exchange, "leave", server.leave(list.get("user"), list.get("group")));
  }

  @Override
  public void send(HttpExchange exchange) throws IOException {
  }

  @Override
  public void groups(HttpExchange exchange) throws IOException {
    handle(exchange, "groups");
  }

  @Override
  public void users(HttpExchange exchange) throws IOException {
    handle(exchange, "users");
  }

  @Override
  public void history(HttpExchange exchange) throws IOException {
    handle(exchange, "history");
  }

  private void handle(HttpExchange exchange, String context, int code) throws IOException {
    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set("Content-Type", "text/plain");
    exchange.sendResponseHeaders(code, 0);
    PrintStream response = new PrintStream(exchange.getResponseBody());
    response.close();
  }

  private void handle(HttpExchange exchange, String context, ResponseBody body) throws IOException {
    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set("Content-Type", "text/plain");
    exchange.sendResponseHeaders(body.code, 0);
    PrintStream response = new PrintStream(exchange.getResponseBody());
    response.println(body.toString());
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

  private HashMap<String, String> getMapFromBody(HttpExchange exchange) throws IOException {
    ObjectInputStream in = new ObjectInputStream(exchange.getRequestBody());
    try {
      return (HashMap<String, String>) in.readObject();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}
