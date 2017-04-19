package csci4311.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    out.println(client.join("tlmader", "general"));
    out.println(client.join("astrika", "general"));
    out.println(client.join("hill", "programming"));
    out.println(client.join("tlmader", "programming"));
    out.println(client.groups());
    out.println(client.users("general"));
    out.println(client.users("programming"));
    List<String> to = new ArrayList<>();
    to.add("#general");
    to.add("#programming");
    out.println(client.send(new MsgpMessage("ted", to, "I'm pooping.")));
    out.println(client.send(new MsgpMessage("astrika", to, "You're a poopster!")));
    out.println(client.history("general"));
    out.println(client.history("programming"));
  }
}
