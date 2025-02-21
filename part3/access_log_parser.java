import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class access_log_parser {

    public static void main(String[] args) {
        String logFilePath = "input/access_log";
        String logEntryPattern = "^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\d+) \"([^\"]*)\" \"([^\"]*)\"";

        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            Pattern pattern = Pattern.compile(logEntryPattern);

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String ipAddress = matcher.group(1);
                    String userIdentifier = matcher.group(2);
                    String userId = matcher.group(3);
                    String timestamp = matcher.group(4);
                    String request = matcher.group(5);
                    String statusCode = matcher.group(6);
                    String bytesSent = matcher.group(7);
                    String referer = matcher.group(8);
                    String userAgent = matcher.group(9);

                    System.out.println("IP Address: " + ipAddress);
                    System.out.println("User Identifier: " + userIdentifier);
                    System.out.println("User ID: " + userId);
                    System.out.println("Timestamp: " + timestamp);
                    System.out.println("Request: " + request);
                    System.out.println("Status Code: " + statusCode);
                    System.out.println("Bytes Sent: " + bytesSent);
                    System.out.println("Referer: " + referer);
                    System.out.println("User Agent: " + userAgent);
                    System.out.println("------------------------------------");
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}