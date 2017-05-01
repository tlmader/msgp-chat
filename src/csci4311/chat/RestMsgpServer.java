package csci4311.chat;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implements RESTful methods for sending and receiving of encoded messages.
 *
 * @author Ted Mader
 * @since 2017-04-30
 */
class RestMsgpServer {

  private final ChatServer server;

  RestMsgpServer(ChatServer server) {
    this.server = server;
  }

  void root(HttpExchange exchange) throws IOException {
    handle(exchange, 404);
  }

  void groups(HttpExchange exchange) throws IOException {
    if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
      handle(exchange, 404);
      return;
    }
    handle(exchange, setToJSON(server.groups(), "groups"), 200);
  }

  void users(HttpExchange exchange) throws IOException {
    if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
      handle(exchange, 404);
      return;
    }
    handle(exchange, setToJSON(server.users(), "users"), 200);
  }

  void group(HttpExchange exchange) throws IOException{
    if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
      usersByGroup(exchange);
    } else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
      addUser(exchange);
    } else if (exchange.getRequestMethod().equalsIgnoreCase("DELETE")) {
      deleteUser(exchange);
    } else {
      handle(exchange, 404);
    }
  }

  private void usersByGroup(HttpExchange exchange) throws IOException {
    String query = exchange.getRequestURI().getPath();
    String group = query.substring(query.lastIndexOf('/') + 1);
    Set<String> userSet = server.users(group);
    if (userSet != null) {
      handle(exchange, setToJSON(server.users(group), "users"), 200);
    } else {
      handle(exchange, 400);
    }
  }

  private void addUser(HttpExchange exchange) throws IOException {
    String query = exchange.getRequestURI().getPath();
    String group = query.substring(query.lastIndexOf('/') + 1);
    String body = getBody(exchange);
    if (body != null && body.startsWith("user=")) {
      String user = body.substring(5);
      if (user.length() == 0) {
        handle(exchange, 400);
        return;
      }
      server.connect(user, null);
      int code = 201;
      if (server.groups().contains(group)) {
        code = 200;
      }
      server.join(user, group);
      Set<String> userSet = server.users(group);
      if (userSet != null) {
        handle(exchange, setToJSON(server.users(group), "users"), code);
      } else {
        handle(exchange, 400);
      }
    }
  }

  private void deleteUser(HttpExchange exchange) throws IOException {
    String query = exchange.getRequestURI().getPath();
    String user = query.substring(query.lastIndexOf('/') + 1);
    query = query.substring(0, query.lastIndexOf('/'));
    String group = query.substring(query.lastIndexOf('/') + 1);
    if (!server.groups().contains(group)) {
      handle(exchange, 400);
    } else if (!server.users(group).contains(user)) {
      handle(exchange, 401);
    } else {
      handle(exchange, server.leave(user, group));
    }
  }

  void messages(HttpExchange exchange) throws IOException {
    if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
      getMessages(exchange);
    } else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
      postMessage(exchange);
    } else {
      handle(exchange, 404);
    }
  }

  private void getMessages(HttpExchange exchange) throws IOException {
    String query = exchange.getRequestURI().getPath();
    String recipient = query.substring(query.lastIndexOf('/') + 1);
    List<MsgpMessage> messages = new ArrayList<>();
    if (recipient.startsWith("@")) {
      if (!server.users().contains(recipient.substring(1))) {
        handle(exchange, 401);
        return;
      }
      for (String group : server.groups()) {
        messages.addAll(server.history(group).stream()
            .filter(x -> x.getFrom().equals(recipient.substring(1)))
            .collect(Collectors.toList()));
      }
    } else {
      if (!server.groups().contains(recipient)) {
        handle(exchange, 400);
        return;
      }
      messages = server.history(recipient);
    }
    handle(exchange, msgpToJSON(messages), 200);
  }

  private void postMessage(HttpExchange exchange) throws IOException {

  }

  private void handle(HttpExchange exchange, int code) throws IOException {
    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set("Content-Type", "application/x-www-form-urlencoded");
    exchange.sendResponseHeaders(code, 0);
    new PrintStream(exchange.getResponseBody()).close();
  }

  private void handle(HttpExchange exchange, String json, int code) throws IOException {
    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set("Content-Type", "application/json");
    exchange.sendResponseHeaders(code, 0);
    PrintStream response = new PrintStream(exchange.getResponseBody());
    response.println(json);
    response.close();
  }

  private String setToJSON(Set<String> set, String key) {
    StringBuilder json = new StringBuilder();
    json.append("[{\"")
        .append(key)
        .append("\":[");
    String prefix = "";
    if (set != null) {
      for (String e : set) {
        json.append(prefix)
            .append("\"")
            .append(e)
            .append("\"");
        prefix = ",";
      }
    }
    json.append("]}]");
    return json.toString();
  }

  private String msgpToJSON(List<MsgpMessage> messages) {
    StringBuilder json = new StringBuilder();
    json.append("[{\"messages\":[");
    String prefix = "";
    if (messages != null) {
      for (MsgpMessage m : messages) {
        json.append(prefix)
            .append("\"from: ")
            .append(m.getFrom());
        for (String to : m.getTo()) {
          json.append("\r\nto: ")
              .append(to);
        }
        json.append("\r\n")
            .append(m.getMessage())
            .append("\r\n\"");
        prefix = ",";
      }
    }
    json.append("]}]");
    return json.toString();
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
    handle(exchange, 400);
    return null;
  }
}
