package csci4311.chat;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Executors;

/**
 * Implements methods for handling core functionality of maintaining users, groups, messages, etc.
 *
 * @author Ted Mader
 */
public class ChatServer implements MessageServer {

  private static final int PORT = 8080;

  HashMap<String, HashSet<String>> groups = new HashMap<>();
  HashSet<String> users = new HashSet<>();

  @Override
  public int join(String user, String group) {
    users.add(user);
    if (!groups.containsKey(group)) {
      groups.put(group, new HashSet<>());
    }
    if (groups.get(group).add(user)) {
      return 200;
    }
    return 201;
  }

  @Override
  public int leave(String user, String group) {
    if (!groups.containsKey(group)) {
      return 400;
    }
    if (groups.get(group).remove(user)) {
      return 200;
    }
    return 201;
  }

  @Override
  public int send(MsgpMessage message) {
    for (String to : message.getTo()) {
      if ((to.startsWith("@") && !users.contains(to)) || (to.startsWith("#") && !groups.containsKey(to))) {
        return 400;
      }
    }
    for (String to : message.getTo()) {
      
    }
    return 200;
  }

  @Override
  public ResponseBody groups(String user) {
    return null;
  }

  @Override
  public ResponseBody users(String user, String group) {
    return null;
  }

  @Override
  public ResponseBody history(String user, String group) {
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
