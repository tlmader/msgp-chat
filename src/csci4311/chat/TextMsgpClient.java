package csci4311.chat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    post("join", user + "/" + group);
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

  private String post(String route, String urlParameters) {
    URL url;
    HttpURLConnection connection = null;
    try {
      // Create connection
      url = new URL(BASE_URL + route);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

      connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
      connection.setRequestProperty("Content-Language", "en-US");

      connection.setUseCaches(false);
      connection.setDoInput(true);
      connection.setDoOutput(true);

      // Send request
      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
      wr.writeBytes(urlParameters);
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
    String targetURL = "http://localhost:8080/send";
    String urlParameters = "test";
    TextMsgpClient client = new TextMsgpClient();
    System.out.println(client.post(targetURL, urlParameters));
  }
}
