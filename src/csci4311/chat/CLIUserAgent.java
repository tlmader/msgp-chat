package csci4311.chat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Scanner;

import static java.lang.System.out;

/**
 * Implements user interaction.
 *
 * @author Ted Mader
 */
public class CLIUserAgent implements UserAgent {

  @Override
  public void deliver(String message) {
    out.println(message);
  }

  private void start() {
    MsgpClient client = new TextMsgpClient();
    String user = "tlmader";
    HttpURLConnection connection = client.connect(user);
    DeliveryWorker worker = new DeliveryWorker(connection);
    worker.start();
    Scanner sc = new Scanner(System.in);
    while (connection != null) {
      out.print("@" + user + " >> ");
      String[] input = sc.nextLine().split(" ");
      switch (input[0]) {
        case "join":
          deliver(input.length == 2 ? client.join(user, input[1]) : getUsage(input[0]));
          break;
        case "leave":
          deliver(input.length == 2 ? client.leave(user, input[1]) : getUsage(input[0]));
          break;
        case "groups":
          deliver(client.groups());
          break;
        case "users":
          deliver(input.length == 2 ? client.users(input[1]) : getUsage(input[0]));
          break;
        case "history":
          deliver(input.length == 2 ? client.history(input[1]) : getUsage(input[0]));
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
        while (connection != null) {
          String line;
          if ((line = rd.readLine()) != null) {
            deliver(line);
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    CLIUserAgent agent = new CLIUserAgent();
    agent.start();
  }
}