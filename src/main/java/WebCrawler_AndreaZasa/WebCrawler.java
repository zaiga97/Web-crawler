package WebCrawler_AndreaZasa;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class is the heart of the web crawler.
 * It's used as an executor service for spawning new {@link PageCrawler}.
 *
 * @author Andrea Zasa
 * @since 09/12/2021
 */
public class WebCrawler {
    private final ExecutorService executor;

    /**
     * Create a new multithreaded web crawler.
     * @param nOfThreads Number of threads used in the web crawler.
     */
    public WebCrawler(int nOfThreads) {
        this.executor = Executors.newFixedThreadPool(nOfThreads);
    }

    /**
     * Add a new runnable to the tasks to do.
     * @param runnable the new runnable
     */
    public void add(Runnable runnable) {
        this.executor.execute(runnable);
    }

    /**
     * Used to shut down safely and wait for unfinished tasks to complete.
     * @param seconds number of seconds to wait before eventually forcing shutdown
     */
    public void shutdown(int seconds) {
        this.executor.shutdown();
        try {
            this.executor.awaitTermination(seconds, TimeUnit.SECONDS);
            if (!this.executor.isTerminated()) {
                //System.err.println("Timed out waiting for executor to terminate cleanly. Shutting down.");
                this.executor.shutdownNow();
            }
        } catch (final InterruptedException e) {
            //System.err.println("Interrupted while waiting for executor shutdown.");
            Thread.currentThread().interrupt();
        }
    }
}
