package WebCrawler_AndreaZasa;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This is a specialized class for providing fairness to the {@link WebCrawler}.
 * For now, it's only checking the crawler can make a request only once per second
 * to each host. But it can be extended to read and deal with robot.txt files.
 * @author Andrea Zasa
 * @since 09/12/2021
 */
public class FairList {
    private final Map<String, FairProperties> fairMap = new HashMap<>();
    public Timer timer;

    ActionListener updateTimer = evt -> {
        synchronized (fairMap){
            fairMap.forEach((k,p) -> p.timeFromLastRequest = p.timeFromLastRequest - 1);
        }
    };

    /**
     * Build a new {@link FairList} and initialize its internal timer.
     * note that when initialized one need to shut it down in the end
     */
    public FairList(){
        timer = new Timer(1, updateTimer);
        timer.start();
    }

    /**
     * This method is used to signal the {@link FairList} that we accessed an url
     * and so the timer for that host has to be reset.
     */
    public void resetTimer(String url){
        String host = getHost(url);
        synchronized (fairMap){
            fairMap.get(host).resetTimeFromLastRequest();
        }
    }

    /**
     * The core method of this class. Ask the class if it's fair to request a URL.
     * @param url The URL we want to see if it's fair to request
     * @return boolean true if it's fair, false otherwise.
     */
    public boolean isFairToRequest(String url){
        String host = getHost(url);
        synchronized (fairMap){
            if (!fairMap.containsKey(host)){
                addToFairMap(host);
            }
            FairProperties properties = fairMap.get(host);
            if (properties.timeFromLastRequest > 0 || properties.notAllowed.contains(url)) return false;
            return true;
        }
    }

    /**
     * Check if the url is in a list of sites the crawler can't access.
     * @param url URL we want to see if we can crawl.
     * @return boolean true if it is allowed, false otherwise
     */
    public boolean isAllowedToRequest(String url){
        String host = getHost(url);
        synchronized (fairMap){
            if (!fairMap.containsKey(host)){
                addToFairMap(host);
            }
            FairProperties properties = fairMap.get(host);
            if (properties.notAllowed.contains(url)) return false;
            return true;
        }
    }

    /**
     * add a host to the {@link FairList}
     * @implNote For now the host is added with default parameter. So no check for the robots.txt is actually performed
     * @param host host to add.
     */
    private void addToFairMap(String host){
        // get the robot.txt and get proprieties

        // add proprieties to fairMap
        synchronized (fairMap){
            fairMap.put(host, new FairProperties());
        }
    }

    private String getHost(String url) {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            return "";
        }
        return uri.getHost();
    }
}

/**
 * DataClass used to store the properties of a host.
 * By default, a delay of 1 sec. is set between requests and no URLs are out of reach on this host.
 */
class FairProperties {
    public int timeFromLastRequest = 0;
    public int delayBetweenRequests = 1;
    public Set<String> notAllowed = new HashSet<>();

    public void resetTimeFromLastRequest(){
        timeFromLastRequest = delayBetweenRequests;
    }
}
