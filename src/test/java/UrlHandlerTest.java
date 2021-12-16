import WebCrawler_AndreaZasa.UrlHandler;
import WebCrawler_AndreaZasa.WebCrawler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrlHandlerTest {

    List<String> testList = new ArrayList<>(List.of("test1", "test2", "test3", "test3"));
    List<String> uniqueTestList = new ArrayList<>(List.of("test1", "test2", "test3"));
    UrlHandler urlHandler = new UrlHandler();

    @Test
    public void ableToAdd(){
        urlHandler.addToVisit(testList);
        assertEquals(testList, urlHandler.getToVisit());
    }

    @Test
    public void uniqueUrl(){
        List<String> alreadyVisited = new ArrayList<>(List.of("test3", "test4"));
        urlHandler.addVisited(alreadyVisited);
        testList.removeAll(alreadyVisited);

        urlHandler.addToVisit(testList);

        assertEquals(testList, urlHandler.getToVisit());
    }

    @Test
    public void concurrentAccess(){
        urlHandler.addToVisit(testList);

        WebCrawler webCrawler = new WebCrawler(10);
        int size = urlHandler.getToVisit().size();
        for (int i = 0; i < size; i++) {
            webCrawler.add(new Runnable() {
                @Override
                public void run() {
                    String url = urlHandler.nextRandom();
                    urlHandler.addVisited(url);
                    //System.out.println("Here thread " + Thread.currentThread().getId() + " working on: " + url);
                }
            });
        }

        webCrawler.shutdown(10);

        //System.out.println(urlHandler.getVisited());
        assertEquals(true, urlHandler.getVisited().containsAll(uniqueTestList));
    }
}
