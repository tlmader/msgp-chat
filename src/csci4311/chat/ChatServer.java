package csci4311.chat;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Implements methods for handling core functionality of maintaining users, groupUsers, messages, etc.
 *
 * @author Ted Mader
 */
public class ChatServer implements MessageServer {

  private static final int PORT = 8080;

  private Map<String, HashSet<String>> groupUsers = new HashMap<>();
  private Map<String, List<String>> groupHistory = new HashMap<>();
  private Set<String> userSet = new HashSet<>();

  @Override
  public int join(String user, String group) {
    userSet.add(user);
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
    for (String to : message.getTo()) {
      if ((to.startsWith("@") && !userSet.contains(to.substring(1))) ||
          (to.startsWith("#") && !groupUsers.containsKey(to.substring(1)))) {
        return 400;
      }
    }
    for (String to : message.getTo()) {
      if (to.startsWith("#")) {
        groupHistory.get(to.substring(1)).add(message.getMessage());
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

  @Override
  public List<MsgpMessage> history(String user, String group) {
    return null;
  }

  public static void main(String[] args) throws IOException {
    InetSocketAddress addr = new InetSocketAddress(PORT);
    HttpServer server = HttpServer.create(addr, 0);
    MsgpServer msgp = new TextMsgpServer();
    server.createContext("/join", msgp::join);
    server.createContext("/leave", msgp::leave);
    server.createContext("/send", msgp::send);
    server.createContext("/groups", msgp::groups);
    server.createContext("/users", msgp::users);
    server.createContext("/history", msgp::history);
    server.setExecutor(Executors.newCachedThreadPool());
    server.start();
    System.out.println("Server is listening on port " + PORT + "...");
  }
}
