import com.panforge.robotstxt.RobotsTxt;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

public class robotTxtTest {
    public static void main(String[] args) {
        RobotsTxt robotsTxt = null;
        try (InputStream robotsTxtStream = new URL("https://github.com/robots.txt").openStream()) {
            robotsTxt = RobotsTxt.read(robotsTxtStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(robotsTxt.query("*","/humans.txt"));
        System.out.println(robotsTxt.getDisallowList("*"));
        System.out.println(robotsTxt.ask("baidu", "").getCrawlDelay());

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = null;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://github.com/robots.txt"))
                .build();
        try {
            response = client.sendAsync(request,  HttpResponse.BodyHandlers.ofString()).get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response != null){
            System.out.println(response.body());
        }
    }

}
