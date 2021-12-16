import WebCrawler_AndreaZasa.WebCrawler;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

public class executorTest {
    WebCrawler webCrawler = new WebCrawler(10);

    @Test
    public void capableOfSpawningThreads(){
        AtomicInteger i = new AtomicInteger(0);

        for (int j = 0; j < 2000; j++) {
            webCrawler.add(new AddOneToAtomicInteger(i));
        }
        webCrawler.shutdown(10);

        assertEquals(2000, i.get());
    }
}

record AddOneToAtomicInteger(AtomicInteger i) implements Runnable {
    @Override
    public void run() {
        try{
            i.incrementAndGet();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
}