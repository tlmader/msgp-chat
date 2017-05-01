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

  private ChatServer server = new ChatServer();

  public void connect(HttpExchange exchange) throws IOException {
    handle(exchange, server.connect(getStringFromBody(exchange), new PrintStream(exchange.getResponseBody())));
  }

  @Override
  public void join(HttpExchange exchange) throws IOException {
    String str;
    if ((str = getStringFromBody(exchange)) != null) {
      String[] strs = str.split("\\s+");
      handle(exchange, strs.length > 3 ? server.join(strs[2], strs[3]) : 400);
    }
  }

  @Override
  public void leave(HttpExchange exchange) throws IOException {
    String str;
    if ((str = getStringFromBody(exchange)) != null) {
      String[] strs = str.split("\\s+");
      handle(exchange, strs.length > 3 ? server.leave(strs[2], strs[3]) : 400);
    }
  }

  @Override
  public void send(HttpExchange exchange) throws IOException {
    String str;
    if ((str = getStringFromBody(exchange)) != null) {
      String[] lines = str.split("[\\r\\n]+");
      Set<String> to = new HashSet<>();
      String from = "", message = "";
      for (String line : lines) {
        if (line.startsWith("to: ")) {
          to.add(line.substring(4));
        } else if (line.startsWith("from: ")) {
          from = line.substring(6);
        } else {
          message = line;
        }
      }
      handle(exchange, server.send(new MsgpMessage(from, to, message)));
    }
  }

  @Override
  public void groups(HttpExchange exchange) throws IOException {
    handle(exchange, server.groups());
  }

  @Override
  public void users(HttpExchange exchange) throws IOException {
    String str;
    if ((str = getStringFromBody(exchange)) != null) {
      String[] strs = str.split("\\s+");
      if (strs.length > 2) {
        handle(exchange, server.users(strs[2]));
      } else {
        handle(exchange, 400);
      }
    }
  }

  @Override
  public void history(HttpExchange exchange) throws IOException {
    String str;
    if ((str = getStringFromBody(exchange)) != null) {
      String[] strs = str.split("\\s+");
      if (strs.length > 2) {
        handle(exchange, server.history(strs[2]), strs[2]);
      } else {
        handle(exchange, 400);
      }
    }
  }

  private void handle(HttpExchange exchange, int code) throws IOException {
    exchange.sendResponseHeaders(code, 0);
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
      response.println("to: " + group);
      response.println("\n" + m.getMessage() + "\n");
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

  private String getStringFromBody(HttpExchange exchange) throws IOException {
    ObjectInputStream in = new ObjectInputStream(exchange.getRequestBody());
    try {
      return (String) in.readObject();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    handle(exchange, 400);
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
