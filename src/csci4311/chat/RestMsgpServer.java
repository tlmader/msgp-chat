package csci4311.chat;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;

/**
 * Description.
 *
 * @author Ted Mader
 * @since 2017-04-30
 */
public class RestMsgpServer implements MsgpServer {

  private final ChatServer server = new ChatServer();

  @Override
  public void connect(HttpExchange exchange) throws IOException {

  }

  @Override
  public void join(HttpExchange exchange) throws IOException {

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
    handle(exchange, "[{\"users\":" + server.users().toString() + "}]" );
  }

  @Override
  public void history(HttpExchange exchange) throws IOException {

  }

  private void handle(HttpExchange exchange, String json) throws IOException {
    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set("Content-Type", "application/json");
    exchange.sendResponseHeaders(200, 0);
    PrintStream response = new PrintStream(exchange.getResponseBody());
    response.println(json);
    response.close();
  }
}
