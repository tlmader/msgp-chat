package csci4311.chat;

import java.util.List;

/**
 * Implements the msgp protocol.
 *
 * @author Ted Mader
 */
public class TextMsgpClient implements MsgpClient {

  @Override
  public int join(String user, String group) {
    return 0;
  }

  @Override
  public int leave(String user, String group) {
    return 0;
  }

  @Override
  public int send(MsgpMessage message) {
    return 0;
  }

  @Override
  public List<String> groups() {
    return null;
  }

  @Override
  public List<String> users(String group) {
    return null;
  }

  @Override
  public List<MsgpMessage> history(String group) {
    return null;
  }
}
