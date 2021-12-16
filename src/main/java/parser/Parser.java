package parser;
import java.util.List;

public interface Parser {
    List<String> getLinks();
    String getContent();

    void parse(String response);
}
