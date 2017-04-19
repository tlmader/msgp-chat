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
    out.println(client.leave("Ted", "CSCI4311"));
    out.println(client.join("Ted", "CSCI4311"));
    out.println(client.leave("Ted", "CSCI4311"));
    out.println(client.leave("Ted", "CSCI4311"));
  }
}
