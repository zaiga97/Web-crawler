package WebCrawler_AndreaZasa;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This is a class for handling the URLs for a webcrawler.
 * The entire class is thread safe and so can be used in a multithreaded environment.
 * It keeps a set of sites already visited: "visited"
 * and a list of the URLs found but not already visited: "toVisit"
 *
 *  * @author Andrea Zasa
 *  * @since 09/12/2021
 */
public class UrlHandler {
    private final Set<String> visited = new HashSet<>();       //URLs already visited
    private final List<String> toVisit = new ArrayList<>();    //URLs found but not yet visited
    private final FairList fairList = new FairList();           //A custom class for dealing with fairness

    /**
     * Add a single URL in String form to the list {@link #toVisit}.
     * @param url URL to add the set {@link #toVisit}
     */
    public void addToVisit(String url) {
        synchronized (this){
            //See if already visited
            if (visited.contains(url)) return;

            //See if it is to index
            if (fairList.isAllowedToRequest(url)){
                toVisit.add(url);
                this.notify();
            }
        }
    }

    /**
     * Add a Collection of URLs in String form to the list {@link #toVisit}.
     * @param urls URLs to add the set {@link #toVisit}
     */
    public void addToVisit(Collection<String> urls) {
        synchronized (this){
            urls.forEach(this::addToVisit);
        }
    }

    /**
     * Add a single URL in String form to the set {@link #visited}.
     * @param url URL to add the set {@link #visited}
     */
    public void addVisited(String url){
        synchronized (this){
            visited.add(url);
        }
    }

    /**
     * Add a Collection of URLs in String form to the set {@link #visited}.
     * @param urls URLs to add the set {@link #visited}
     */
    public void addVisited(Collection<String> urls){
        synchronized (this){
            visited.addAll(urls);
        }
    }

    /**
     * This method return the first member of the URL {@link #toVisit}.
     * If the list is empty it wait for other threads to add some new URLs.
     * @return String the next URL in an order fashion.
     * @deprecated
     */
    @Deprecated
    public String next() {
        synchronized (this){
            // Check if list is empty and wait other to fill it if this is the case.
            while (toVisit.size() == 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String url = toVisit.get(0);
            if (fairList.isFairToRequest(url)){
                toVisit.remove(0);
                return url;
            }
            else return next();
        }
    }

    /**
     * This method return a random member of the URL {@link #toVisit}.
     * It also checks to see if it's fair to request it and otherwise return another URL.
     * If the list is empty it wait for other threads to add some new URLs.
     * @return String the next URL in an order fashion.
     */
    public String nextRandom() {
        synchronized (this){
            // Check if list is empty and wait other to fill it if this is the case.
            while (toVisit.size() == 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Get a random index to access the list
            int n = 0;
            if (toVisit.size() > 1){
                n = ThreadLocalRandom.current().nextInt(toVisit.size());
            }
            // Pop the URL from the list and return it
            String url = toVisit.get(n);
            // Check if it is fair to request this Server
            if (fairList.isFairToRequest(url)){
                fairList.resetTimer(url);
                toVisit.remove(n);
                return url;
            }
            else{
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return nextRandom();
            }
        }
    }

    /**
     * This method return a copy of the {@link #toVisit} list
     * @return List copy of {@link #toVisit}
     */
    public List<String> getToVisit() {
        synchronized (this){
            return List.copyOf(toVisit);
        }
    }

    /**
     * This method return a copy of the {@link #visited} set
     * @return Set copy of {@link #visited}
     */
    public Set<String> getVisited() {
        synchronized (this){
            return Set.copyOf(visited);
        }
    }

    public void shutdown(){
        fairList.timer.stop();
    }
}