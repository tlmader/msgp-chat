package csci4311.chat;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;

/**
 * Implements methods for sending and receiving of encoded messages.
 *
 * @author Ted Mader
 */
public class TextMsgpServer implements MsgpServer {

  @Override
  public void join(HttpExchange exchange) {

  }

  @Override
  public void leave(HttpExchange exchange) throws IOException {

  }

  @Override
  public void send(HttpExchange exchange) throws IOException {

  }

  @Override
  public void groups(HttpExchange exchange) throws IOException {

  }

  @Override
  public void users(HttpExchange exchange) throws IOException {
    String requestMethod = exchange.getRequestMethod();

    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set( "Content-Type", "text/plain");
    exchange.sendResponseHeaders( 200, 0);

    PrintStream response = new PrintStream( exchange.getResponseBody());
    response.println( "context: /users; method: " + requestMethod);
    printHeaders( exchange, response);
    response.close();
  }

  @Override
  public void history(HttpExchange exchange) throws IOException {

  }

  private void printHeaders( HttpExchange exchange, PrintStream response) {
    Headers requestHeaders = exchange.getRequestHeaders();
    Set<String> keySet = requestHeaders.keySet();
    for (String key : keySet) {
      response.println(key + " = " + requestHeaders.get(key));
    }
  }
}
