package csci4311.chat;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.util.*;

/**
 * Implements methods for sending and receiving of encoded messages.
 *
 * @author Ted Mader
 */
@SuppressWarnings("unchecked")
public class  TextMsgpServer implements MsgpServer {

  private final MessageServer server;

  TextMsgpServer(MessageServer server) {
    this.server = server;
  }

  public void connect(HttpExchange exchange) throws IOException {
    server.connect(getBody(exchange), new PrintStream(exchange.getResponseBody()));
    handleWithoutClose(exchange);
  }

  @Override
  public void root(HttpExchange exchange) throws IOException {
    handle(exchange, 404);
  }

  @Override
  public void join(HttpExchange exchange) throws IOException {
    String body = getBody(exchange);
    if (body != null) {
      String[] strs = body.split("\\s+");
      handle(exchange, strs.length > 3 ? server.join(strs[2], strs[3]) : 400);
    }
  }

  @Override
  public void leave(HttpExchange exchange) throws IOException {
    String body = getBody(exchange);
    if (body != null) {
      String[] strs = body.split("\\s+");
      handle(exchange, strs.length > 3 ? server.leave(strs[2], strs[3]) : 400);
    }
  }

  @Override
  public void send(HttpExchange exchange) throws IOException {
    MsgpMessage message = getMessageFromBody(exchange);
    if (message != null) {
      handle(exchange, server.send(message));
    }
  }

  @Override
  public void groups(HttpExchange exchange) throws IOException {
    handle(exchange, server.groups());
  }

  @Override
  public void users(HttpExchange exchange) throws IOException {
    String body = getBody(exchange);
    if (body != null) {
      String[] strs = body.split("\\s+");
      if (strs.length > 2) {
        handle(exchange, server.users(strs[2]));
      } else {
        handle(exchange, 500);
      }
    }
  }

  @Override
  public void history(HttpExchange exchange) throws IOException {
    String body;
    if ((body = getBody(exchange)) != null) {
      String[] strs = body.split("\\s+");
      if (strs.length > 2) {
        handle(exchange, server.history(strs[2]), strs[2]);
      } else {
        handle(exchange, 500);
      }
    }
  }

  private void handle(HttpExchange exchange, int code) throws IOException {
    exchange.sendResponseHeaders(code, 0);
    new PrintStream(exchange.getResponseBody()).close();
  }

  private void handleWithoutClose(HttpExchange exchange) throws IOException {
    exchange.sendResponseHeaders(200, 0);
  }

  private void handle(HttpExchange exchange, Set<String> set) throws IOException {
    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set("Content-Type", "text/plain");
    int code = set.isEmpty() ? 201 : 200;
    exchange.sendResponseHeaders(code, 0);

    PrintStream response = new PrintStream(exchange.getResponseBody());
    printReplyCode(response, code);
    for (String s : set) {
      response.println(s);
    }
    response.close();
  }

  private void handle(HttpExchange exchange, List<MsgpMessage> messages, String group) throws IOException {
    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set("Content-Type", "text/plain");
    int code = messages.isEmpty() ? 201 : 200;
    exchange.sendResponseHeaders(code, 0);

    PrintStream response = new PrintStream(exchange.getResponseBody());
    printReplyCode(response, code);
    for (MsgpMessage m : messages) {
      response.println("msgp send");
      response.println("from: " + m.getFrom());
      response.println("to: #" + group);
      response.println("\r\n" + m.getMessage() + "\r\n");
    }
    response.close();
  }

  private void printReplyCode(PrintStream response, int code) {
    response.print("msgp " + code);
    switch (code) {
      case 200:
        response.println(" OK");
        break;
      case 201:
        response.println(" No result");
        break;
      case 400:
        response.println(" Error");
        break;
    }
  }

  @SuppressWarnings("Duplicates")
  private String getBody(HttpExchange exchange) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
    StringBuilder body = new StringBuilder();
    String line, prefix = "";
    while ((line = in.readLine()) != null) {
      body.append(prefix).append(line);
      prefix = "\r\n";
    }
    if (body.length() > 0) {
      return body.toString();
    }
    handle(exchange, 500);
    return null;
  }

  @SuppressWarnings("Duplicates")
  private MsgpMessage getMessageFromBody(HttpExchange exchange) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
    Set<String> to = new HashSet<>();
    String line, from = "", message = "";
    while ((line = in.readLine()) != null) {
      if (line.startsWith("to: ")) {
        to.add(line.substring(4));
      } else if (line.startsWith("from: ")) {
        from = line.substring(6);
      } else {
        message = line;
      }
    }
    return new MsgpMessage(from, to, message);
  }
}
