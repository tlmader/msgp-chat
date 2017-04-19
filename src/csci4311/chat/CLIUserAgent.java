package csci4311.chat;

import java.util.Scanner;

import static java.lang.System.out;

/**
 * Implements user interaction.
 *
 * @author Ted Mader
 */
public class CLIUserAgent implements UserAgent {

  @Override
  public void deliver(MsgpMessage message) {

  }

  public static void main(String[] args) {
    TextMsgpClient client = new TextMsgpClient();
    Scanner sc = new Scanner(System.in);
    String user = "tlmader";
    while (true) {
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

  private static String getUsage(String command) {
    return "usage: " + command + " <group>";
  }
}
//    out.println(client.join("tlmader", "general"));
//    out.println(client.join("astrika", "general"));
//    out.println(client.join("hill", "programming"));
//    out.println(client.join("tlmader", "programming"));
//    out.println(client.groups());
//    out.println(client.users("general"));
//    out.println(client.users("programming"));
//    List<String> to = new ArrayList<>();
//    to.add("#general");
//    to.add("#programming");
//    out.println(client.send(new MsgpMessage("ted", to, "I'm pooping.")));
//    out.println(client.send(new MsgpMessage("astrika", to, "You're a poopster!")));
//    out.println(client.history("general"));
//    out.println(client.history("programming"));