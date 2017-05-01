package csci4311.chat;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Implements methods for handling core functionality of maintaining users, groupUsers, messages, etc.
 *
 * @author Ted Mader
 */
public class ChatServer implements MessageServer {

  private Map<String, HashSet<String>> groupUsers = new HashMap<>();
  private Map<String, List<MsgpMessage>> groupHistory = new HashMap<>();
  private Map<String, PrintStream> userStreams = new HashMap<>();

  @Override
  public int connect(String user, PrintStream ps) {
    userStreams.put(user, ps);
    return 200;
  }

  @Override
  public int join(String user, String group) {
    if (!userStreams.containsKey(user)) {
      return 400;
    }
    if (!groupUsers.containsKey(group)) {
      groupUsers.put(group, new HashSet<>());
      groupHistory.put(group, new ArrayList<>());
    }
    if (groupUsers.get(group).add(user)) {
      return 200;
    }
    return 201;
  }

  @Override
  public int leave(String user, String group) {
    if (!groupUsers.containsKey(group)) {
      return 400;
    }
    if (groupUsers.get(group).remove(user)) {
      return 200;
    }
    return 201;
  }

  @Override
  public int send(MsgpMessage message) {
    if (!userStreams.containsKey(message.getFrom())) {
      return 400;
    }
    for (String to : message.getTo()) {
      if ((to.startsWith("@") && !userStreams.containsKey(to.substring(1))) ||
          (to.startsWith("#") && !groupUsers.containsKey(to.substring(1)))) {
        return 400;
      }
    }
    for (String to : message.getTo()) {
      if (to.startsWith("@") && !to.substring(1).equals(message.getFrom())) {
        deliver(to.substring(1), message);
      } else if (to.startsWith("#")) {
        for (String user : groupUsers.get(to.substring(1))) {
          deliver(user, message);
        }
        groupHistory.get(to.substring(1)).add(message);
      }
    }
    return 200;
  }

  @Override
  public Set<String> groups() {
    return groupUsers.keySet();
  }

  @Override
  public Set<String> users(String group) {
    return groupUsers.get(group);
  }

  public Set<String> users() {
    return userStreams.keySet();
  }

  @Override
  public List<MsgpMessage> history(String group) {
    return groupHistory.get(group);
  }

  private void deliver(String to, MsgpMessage message) {
    if (to.equals(message.getFrom())) {
      return;
    }
    PrintStream ps = userStreams.get(to);
    ps.println("msgp send");
    ps.println("from: " + message.getFrom());
    ps.println("to: " + to);
    ps.println("\r\n" + message.getMessage() + "\r\n");
    ps.close();
  }

  public static void main(String[] args) throws IOException {
    int port = args.length > 0 ? Integer.parseInt(args[0]) : 4311;
    int restPort = args.length > 1 ? Integer.parseInt(args[1]) : 8311;
    ChatServer chatServer = new ChatServer();
    // Start text server
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
    MsgpServer msgp = new TextMsgpServer(chatServer);
    server.createContext("/", msgp::root);
    server.createContext("/connect", msgp::connect);
    server.createContext("/join", msgp::join);
    server.createContext("/leave", msgp::leave);
    server.createContext("/send", msgp::send);
    server.createContext("/groups", msgp::groups);
    server.createContext("/users", msgp::users);
    server.createContext("/history", msgp::history);
    server.setExecutor(Executors.newCachedThreadPool());
    server.start();
    // Start REST API
    HttpServer restServer = HttpServer.create(new InetSocketAddress(restPort), 0);
    RestMsgpServer restMsgp = new RestMsgpServer(chatServer);
    restServer.createContext("/", restMsgp::root);
    restServer.createContext("/users", restMsgp::users);
    restServer.createContext("/groups", restMsgp::groups);
    restServer.createContext("/group/", restMsgp::group);
    restServer.createContext("/messages", restMsgp::messages);
    restServer.setExecutor(Executors.newCachedThreadPool());
    restServer.start();
    System.out.println("Server is listening on ports " + port + " and " + restPort + " (REST API)...");
  }
}
