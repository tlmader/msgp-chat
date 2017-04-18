package csci4311.chat;

/**
 * TODO: Add description.
 *
 * @author Ted Mader
 */
public class ResponseBody {
  int code;
  String message;

  ResponseBody(int code, String message) {
    this.code = code;
    this.message = message;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("msgp ");
    switch (code) {
      case 200:
        sb.append("200 OK");
        break;
      case 201:
        sb.append("201 No result");
        break;
      default:
        sb.append("400 Error");
        break;
    }
    sb.append("\n");
    sb.append(message);
    return sb.toString();
  }
}
