package csci4311.chat;

/**
 * Implements user interaction.
 *
 * @author Ted Mader
 */
public class RESTUserAgent extends CLIUserAgent {

  public RESTUserAgent(MsgpClient client) {
    super(client);
  }

  public static void main(String[] args) {
    CLIUserAgent agent = new CLIUserAgent(new TextMsgpClient(
        args.length > 1 ? args[1] : "http://localhost",
        args.length > 2 ? args[2] : "4311"));
    agent.user = args.length > 0 ? args[0] : "tlmader";
    agent.start();
  }
}
