package csci4311.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.out;

/**
 * Implements user interaction.
 *
 * @author Ted Mader
 */
public class CLIUserAgent implements UserAgent {

  private String user = "tlmader";
  private MsgpClient client = new TextMsgpClient();

  @Override
  public void deliver(String message) {
    out.println(message);
    HttpURLConnection connection = client.connect(user);
    new DeliveryWorker(connection).start();
  }

  private void start() {
    HttpURLConnection connection = client.connect(user);
    new DeliveryWorker(connection).start();
    Scanner sc = new Scanner(System.in);
    while (connection != null) {
      out.print("@" + user + " >> ");
      String input = sc.nextLine();
      String[] inputArr = input.split(" ");
      switch (inputArr[0]) {
        case "join":
          deliver(inputArr.length == 2 ? client.join(user, inputArr[1]) : getUsage(inputArr[0]));
          break;
        case "leave":
          deliver(inputArr.length == 2 ? client.leave(user, inputArr[1]) : getUsage(inputArr[0]));
          break;
        case "groups":
          deliver(client.groups());
          break;
        case "users":
          deliver(inputArr.length == 2 ? client.users(inputArr[1]) : getUsage(inputArr[0]));
          break;
        case "send":
          List<String> to = new ArrayList<>();
          to.add("@tlmader");
          client.send(new MsgpMessage("tlmader", to, "Success!"));
          break;
        case "history":
          deliver(inputArr.length == 2 ? client.history(inputArr[1]) : getUsage(inputArr[0]));
          break;
      }
    }
  }

  private String getUsage(String command) {
    return "usage: " + command + " <group>";
  }

  private class DeliveryWorker extends Thread {

    private HttpURLConnection connection;

    DeliveryWorker(HttpURLConnection connection) {
      this.connection = connection;
    }

    public void run() {
      try {
        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = null;
        while (line == null) {
          if ((line = rd.readLine()) != null) {
            deliver(line);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    CLIUserAgent agent = new CLIUserAgent();
    agent.start();
  }
}