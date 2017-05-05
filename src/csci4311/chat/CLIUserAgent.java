package csci4311.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static java.lang.System.out;

/**
 * Implements user interaction.
 *
 * @author Ted Mader
 */
public class CLIUserAgent implements UserAgent {

  protected String user;
  private MsgpClient client;

  public CLIUserAgent(MsgpClient client) {
    this.client = client;
  }

  @Override
  public void deliver(String message) {
    out.println("\r\n" + message);
    out.print("@" + user + " >> ");
    HttpURLConnection connection = client.connect(user);
    new DeliveryWorker(connection).start();
  }

  void start() {
    HttpURLConnection connection = client.connect(user);
    new DeliveryWorker(connection).start();
    Scanner sc = new Scanner(System.in);
    while (connection != null) {
      out.print("@" + user + " >> ");
      String input = sc.nextLine();
      String[] inputArr = input.split("\\s+");
      switch (inputArr[0]) {
        case "join":
          out.println(inputArr.length == 2 ? client.join(user, inputArr[1].replace("#", "")) : getUsage(inputArr[0]));
          break;
        case "leave":
          out.println(inputArr.length == 2 ? client.leave(user, inputArr[1].replace("#", "")) : getUsage(inputArr[0]));
          break;
        case "groups":
          out.println(client.groups());
          break;
        case "users":
          out.println(inputArr.length == 2 ? client.users(inputArr[1].replace("#", "")) : getUsage(inputArr[0]));
          break;
        case "send":
          Set<String> to = new HashSet<>();
          String message = null;
          for (int i = 1; i < inputArr.length; i++) {
            if (inputArr[i].startsWith("@") || inputArr[i].startsWith("#") && !inputArr[i].substring(1).equals(user)) {
              to.add(inputArr[i]);
            } else {
              message = String.join(" ", Arrays.copyOfRange(inputArr, i, inputArr.length));
              break;
            }
          }
          client.send(new MsgpMessage(user, to, message));
          break;
        case "history":
          out.println(inputArr.length == 2 ? client.history(inputArr[1].replace("#", "")) : getUsage(inputArr[0]));
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
        //noinspection StatementWithEmptyBody
        while (rd.readLine() == null) {
          // Wait for message
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(rd.readLine().substring(6));
        sb.append("] ");
        rd.readLine();
        rd.readLine();
        sb.append(rd.readLine());
        deliver(sb.toString());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    CLIUserAgent agent = new CLIUserAgent(new TextMsgpClient(
        args.length > 1 ? args[1] : "http://localhost",
        args.length > 2 ? args[2] : "4311"));
    agent.user = args.length > 0 ? args[0] : "tlmader";
    agent.start();
  }
}