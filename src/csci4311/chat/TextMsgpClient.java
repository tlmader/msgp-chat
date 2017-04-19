package csci4311.chat;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Implements the msgp protocol.
 *
 * @author Ted Mader
 */
public class TextMsgpClient implements MsgpClient {

  private final String BASE_URL = "http://localhost:8080/";

  @Override
  public int join(String user, String group) {
    HashMap<String, String> body = new HashMap<>();
    body.put("user", user);
    body.put("group", group);
    return getResponseCode(createConnection("join", body));
  }

  @Override
  public int leave(String user, String group) {
    HashMap<String, String> body = new HashMap<>();
    body.put("user", user);
    body.put("group", group);
    return getResponseCode(createConnection("leave", body));
  }

  @Override
  public int send(MsgpMessage message) {
    return getResponseCode(createConnection("send", message));
  }

  @Override
  public String groups() {
    return getResponseBody(createConnection("groups"));
  }

  @Override
  public Set<String> users(String group) {
    return null;
  }

  @Override
  public List<MsgpMessage> history(String group) {
    return null;
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
      connection.setDoOutput(true);
      return connection;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private HttpURLConnection createConnection(String route, Serializable obj) {
    URL url;
    HttpURLConnection connection = null;
    try {
      url = new URL(BASE_URL + route);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

      connection.setRequestProperty("Content-Length", "" + Integer.toString(obj.toString().getBytes().length));
      connection.setRequestProperty("Content-Language", "en-US");

      connection.setUseCaches(false);
      connection.setDoInput(true);
      connection.setDoOutput(true);

      // Send request
      ObjectOutputStream wr = new ObjectOutputStream(connection.getOutputStream());
      wr.writeObject(obj);
      wr.flush();
      wr.close();
      return connection;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private int getResponseCode(HttpURLConnection connection) {
    try {
      return connection.getResponseCode();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return 0;
  }

  private String getResponseBody(HttpURLConnection connection) {
    try {
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      String line;
      StringBuilder response = new StringBuilder();
      while ((line = rd.readLine()) != null) {
        response.append(line);
        response.append('\n');
      }
      rd.close();
      return response.toString();

    } catch (Exception e) {

      e.printStackTrace();
      return null;

    } finally {

      if (connection != null) {
        connection.disconnect();
      }
    }
  }
}