package csci4311.chat;

import java.util.ArrayList;
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
    out.println(client.join("tlmader", "programming"));
    List<String> to = new ArrayList<>();
    to.add("#general");
    to.add("#programming");
    out.println(client.send(new MsgpMessage("tlmader", to, "Hello World!")));
  }
}
