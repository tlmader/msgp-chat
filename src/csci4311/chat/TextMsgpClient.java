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

  private final String BASE_URL = "http://localhost:8080/";

  @Override
  public int join(String user, String group) {
    ArrayList<String> list = new ArrayList<>();
    list.add(user);
    list.add(group);
    post("join", list);
    return 0;
  }

  @Override
  public int leave(String user, String group) {
    return 0;
  }

  @Override
  public int send(MsgpMessage message) {
    return 0;
  }

  @Override
  public List<String> groups() {
    return null;
  }

  @Override
  public List<String> users(String group) {
    return null;
  }

  @Override
  public List<MsgpMessage> history(String group) {
    return null;
  }

  private String post(String route, Serializable obj) {
    URL url;
    HttpURLConnection connection = null;
    try {
      // Create connection
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

      // Get Response
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      String line;
      StringBuilder response = new StringBuilder();
      while ((line = rd.readLine()) != null) {
        response.append(line);
        response.append('\r');
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

  public static void main(String[] args) {
    TextMsgpClient client = new TextMsgpClient();
    client.join("Ted", "CSCI4311");
  }
}
