package csci4311.chat;

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
    client.join("Ted", "CSCI4311");
  }
}
