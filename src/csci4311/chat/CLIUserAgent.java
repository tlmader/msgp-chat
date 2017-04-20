package csci4311.chat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import static java.lang.System.out;

/**
 * Implements user interaction.
 *
 * @author Ted Mader
 */
public class CLIUserAgent implements UserAgent {

  private static TextMsgpClient client;

  @Override
  public void deliver(String message) {
    out.println(message);
  }

  private void start() {
    client = new TextMsgpClient();
    String user = "tlmader";
    client.connect(user);
    Scanner sc = new Scanner(System.in);
    DeliveryWorker worker = new DeliveryWorker();
    while (client.userConnection != null) {
      out.print("@" + user + " >> ");
      String[] input = sc.nextLine().split(" ");
      switch (input[0]) {
        case "join":
          out.println(input.length == 2 ? client.join(user, input[1]) : getUsage(input[0]));
          break;
        case "leave":
          out.println(input.length == 2 ? client.leave(user, input[1]) : getUsage(input[0]));
          break;
        case "groups":
          out.println(client.groups());
          break;
        case "users":
          out.println(input.length == 2 ? client.users(input[1]) : getUsage(input[0]));
          break;
        case "history":
          out.println(input.length == 2 ? client.history(input[1]) : getUsage(input[0]));
          break;
      }
    }
  }

  private String getUsage(String command) {
    return "usage: " + command + " <group>";
  }

  private class DeliveryWorker extends Thread {

    public void run() {
      try {
        BufferedReader rd = new BufferedReader(new InputStreamReader(client.userConnection.getInputStream()));
        while (client.userConnection != null) {
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