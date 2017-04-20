package csci4311.chat;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Implements the msgp protocol.
 *
 * @author Ted Mader
 */
public class TextMsgpClient implements MsgpClient {

  private final String BASE_URL = "http://localhost:" + ChatServer.PORT + "/";
  public HttpURLConnection userConnection;

  public void connect(String user) {
    userConnection = createConnection("", user);
  }

  @Override
  public String join(String user, String group) {
    HashMap<String, String> body = new HashMap<>();
    body.put("user", user);
    body.put("group", group);
    int code = getResponseCode(createConnection("join", body));
    if (code == 200) {
      List<String> users = getResponseAsStrings(createConnection("users", group));
      if (users == null) {
        return "Error";
      }
      return "Joined #" + group + " with " + users.size() + " current members.";
    }
    return "Already a member of #" + group + ".";
  }

  @Override
  public String leave(String user, String group) {
    HashMap<String, String> body = new HashMap<>();
    body.put("user", user);
    body.put("group", group);
    int code = getResponseCode(createConnection("leave", body));
    return code == 400 ? "Not a member of #" + group + "." : "";
  }

  @Override
  public int send(MsgpMessage message) {
    return getResponseCode(createConnection("send", message));
  }

  @Override
  public String groups() {
    List<String> groups = getResponseAsStrings(createConnection("groups"));
    StringBuilder out = new StringBuilder();
    if (groups != null) {
      for (String group : groups) {
        List<String> users = getResponseAsStrings(createConnection("users", group));
        if (users == null) {
          continue;
        }
        out.append("#")
            .append(group)
            .append(" has ")
            .append(users.size())
            .append(" members\n");
      }
      return out.toString();
    }
    return "There are currently no groups.";
  }

  @Override
  public String users(String group) {
    List<String> users = getResponseAsStrings(createConnection("users", group));
    StringBuilder out = new StringBuilder();
    if (users != null) {
      for (String user : users) {
        out.append("@")
            .append(user)
            .append("\n");
      }
      return out.toString();
    }
    return "Group " + group + " does not exist.";
  }

  @Override
  public String history(String group) {
    List<String> lines = getResponseAsStrings(createConnection("history", group));
    StringBuilder out = new StringBuilder();
    if (lines != null) {
      for (String line : lines) {
        if (line.startsWith("to: ")) {
          continue;
        }
        if (line.startsWith("from: ")) {
          out.append("[")
              .append(line.substring(6))
              .append("] ");
        } else {
          out.append(line)
              .append("\n");
        }
      }
    }
    return out.toString();
  }

  private HttpURLConnection createConnection(String route) {
    URL url;
    HttpURLConnection connection = null;
    try {
      url = new URL(BASE_URL + route);
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

  private HttpURLConnection createConnection(String route, Serializable obj) {
    HttpURLConnection connection = createConnection(route);
    if (connection == null) {
      return null;
    }
    try {
      connection.setRequestProperty("Content-Length", "" + Integer.toString(obj.toString().getBytes().length));
      connection.setDoOutput(true);
      ObjectOutputStream wr = new ObjectOutputStream(connection.getOutputStream());
      wr.writeObject(obj);
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

  private String getResponseBody(HttpURLConnection connection) {
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String line;
      StringBuilder response = new StringBuilder();
      while ((line = rd.readLine()) != null) {
        response.append(line).append('\n');
      }
      rd.close();
      return response.toString();

    } catch (Exception e) {
      return null;
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