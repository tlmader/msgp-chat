package csci4311.chat;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Set;

/**
 * Implements methods for sending and receiving of encoded messages.
 *
 * @author Ted Mader
 */
public class TextMsgpServer implements MsgpServer {

  @Override
  public void join(HttpExchange exchange) throws IOException {
    handle(exchange, "join");
  }

  @Override
  public void leave(HttpExchange exchange) throws IOException {
    handle(exchange, "leave");
  }

  @Override
  public void send(HttpExchange exchange) throws IOException {
    handle(exchange, "send");
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
}
