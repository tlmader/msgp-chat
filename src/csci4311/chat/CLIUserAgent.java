package csci4311.chat;

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
    out.println(client.join("tlmader", "programming"));
    out.println(client.groups());
  }
}
