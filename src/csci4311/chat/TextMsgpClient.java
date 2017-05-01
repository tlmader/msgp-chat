package csci4311.chat;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the msgp protocol.
 *
 * @author Ted Mader
 */
public class TextMsgpClient implements MsgpClient {

  static String server = "http://localhost";
  static String port = "1337";

  @Override
  public HttpURLConnection connect(String user) {
    return createConnection("connect", user);
  }

  @Override
  public String join(String user, String group) {
    int code = getResponseCode(createConnection("join", "msgp join " + user + " " + group));
    if (code == 200) {
      List<String> users = getResponseAsStrings(createConnection("users", "msgp users " + group));
      if (users == null) {
        return "Error";
      }
      return "Joined #" + group + " with " + users.size() + " current members.";
    } else if (code == 201) {
      return "Already a member of #" + group + ".";
    }
    return "Error";
  }

  @Override
  public String leave(String user, String group) {
    int code = getResponseCode(createConnection("leave", "msgp leave " + user + " " + group));
    return code == 400 ? "Not a member of #" + group + "." : "Left group #" + group + ".";
  }

  @Override
  public int send(MsgpMessage message) {
    StringBuilder out = new StringBuilder();
    out.append("msgp send\nfrom: ")
        .append(message.getFrom());
    for (String to : message.getTo()) {
      out.append("\nto: ")
          .append(to);
    }
    out.append("\n\n")
        .append(message.getMessage())
        .append("\n\n");
    return getResponseCode(createConnection("send", out.toString()));
  }

  @Override
  public String groups() {
    List<String> groups = getResponseAsStrings(createConnection("groups", "msgp groups"));
    StringBuilder out = new StringBuilder();
    if (groups != null && !groups.isEmpty()) {
      for (String group : groups) {
        List<String> users = getResponseAsStrings(createConnection("users", "msgp users " + group));
        out.append("#")
            .append(group)
            .append(" has ")
            .append(users != null ? users.size() : "0")
            .append(" members");
      }
      return out.toString();
    }
    return "There are currently no groups.";
  }

  @Override
  public String users(String group) {
    List<String> users = getResponseAsStrings(createConnection("users", "msgp users " + group));
    StringBuilder out = new StringBuilder();
    if (users != null && !users.isEmpty()) {
      String prefix = "";
      for (String user : users) {
        out.append(prefix)
            .append("@")
            .append(user);
        prefix = "\n";
      }
      return out.toString();
    }
    return "Group " + group + " does not exist.";
  }

  @Override
  public String history(String group) {
    List<String> lines = getResponseAsStrings(createConnection("history", "msgp history " + group));
    StringBuilder out = new StringBuilder();
    if (lines != null && !lines.isEmpty()) {
      String prefix = "";
      for (String line : lines) {
        if (line.startsWith("to: ")) {
          continue;
        }
        if (line.startsWith("from: ")) {
          out.append(prefix);
          out.append("[")
              .append(line.substring(6))
              .append("] ");
        } else {
          out.append(line);
        }
        prefix = "\n";
      }
    }
    return out.toString();
  }

  private HttpURLConnection createConnection(String route) {
    HttpURLConnection connection = null;
    try {
      URL url = new URL(server + ":" + port + "/" + route);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.setRequestProperty("Content-Language", "en-US");
      connection.setUseCaches(false);
      connection.setDoInput(true);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return connection;
  }

  private HttpURLConnection createConnection(String route, String message) {
    HttpURLConnection connection = createConnection(route);
    if (connection == null) {
      return null;
    }
    try {
      connection.setRequestProperty("Content-Length", "" + Integer.toString(message.length()));
      connection.setDoOutput(true);
      OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
      wr.write(message);
      wr.flush();
      wr.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return connection;
  }

  private int getResponseCode(HttpURLConnection connection) {
    try {
      return connection.getResponseCode();
    } catch (IOException e) {
      return 0;
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  private List<String> getResponseAsStrings(HttpURLConnection connection) {
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      List<String> response = new ArrayList<>();
      String line;
      while ((line = rd.readLine()) != null) {
        if (line.startsWith("msgp") || line.length() == 0) {
          continue;
        }
        response.add(line);
      }
      return response.isEmpty() ? null : response;

    } catch (Exception e) {
      return null;
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }
}