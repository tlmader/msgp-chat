package csci4311.chat;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Set;

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

  void usersByGroup(HttpExchange exchange) throws IOException {
    String query = exchange.getRequestURI().getPath();
    String group = query.substring(query.lastIndexOf('/') + 1);
    Set<String> userSet = server.users(group);
    if (userSet != null) {
      handle(exchange, setToJSON(server.users(group), "users"), 200);
    } else {
      handle(exchange, 400);
    }
  }

  void addUser(HttpExchange exchange) throws IOException {
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

  void deleteUser(HttpExchange exchange) throws IOException {

  }

  void history(HttpExchange exchange) throws IOException {

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

  private String getBody(HttpExchange exchange) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
    StringBuilder body = new StringBuilder();
    String line, prefix = "";
    while ((line = in.readLine()) != null) {
      body.append(prefix).append(line);
      prefix = "\n";
    }
    if (body.length() > 0) {
      return body.toString();
    }
    handle(exchange, 400);
    return null;
  }
}
