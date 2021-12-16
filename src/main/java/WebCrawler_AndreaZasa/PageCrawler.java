package WebCrawler_AndreaZasa; /**
 * @autor Andrea Zasa
 * @since 09/12/2021
 */

import parser.JsoupParser;
import parser.Parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * This class is and implementation of runnable used to navigate a URL.
 * It can make an async request to the server and parse its response
 * For parsing the JsoupParser class is used, but it could be substituted by any other Parser.
 * In addition, an Url handler is passed, so it can update its lists with the new-found URLs.
 * A Buffered writer is passed for saving what it's found on a file.
 *
 * @author Andrea Zasa
 * @since 09/12/2021
 */
public class PageCrawler implements Runnable {
    private final String url;
    private final UrlHandler urlHandler;

    private final HttpClient client = HttpClient.newHttpClient();
    private HttpRequest request;
    private HttpResponse<String> response;
    private final Parser parser = new JsoupParser();

    private final List<String> linksOnPage = new LinkedList<>();
    private String content;

    private final BufferedWriter outStream;

    /**
     * The runnable is initialized using 3 parameters:
     * @param url The URL to navigate and parse.
     * @param urlHandler an {@link UrlHandler} for adding the new-found URLs
     * @param outStream an outStream where to save the results of the search
     */
    public PageCrawler(String url, UrlHandler urlHandler, BufferedWriter outStream) {
        this.url = url;
        this.urlHandler = urlHandler;
        this.outStream = outStream;
    }

    /**
     * Build a URL request to pass to the client
     * @param url url for witch we want to make the request
     */
    private void makeRequestFromUrl(String url){
        this.request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
    }

    /**
     * send a URL request asynchronously to the client and wait at most 15 second for the response.
     * @return true if the request was answered in time. false otherwise
     */
    private boolean sendRequest(){
        try {
            CompletableFuture<HttpResponse<String>> completableResponse = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            this.response = completableResponse.get(15, TimeUnit.SECONDS);
        }
        catch (Exception e) { return false; }

        return true;
    }

    /**
     * Parse the response. extract the body of the web page and all the links on it.
     */
    private void parseResponse(){
        parser.parse(response.body());
        linksOnPage.addAll(parser.getLinks());
        content = parser.getContent();
    }

    /**
     * Override of the run method.
     * The WebCrawler_AndreaZasa.PageCrawler start transforming the URL in a Http request.
     * The request is sent asynchronously via the client.
     * if a response is received in time then the page is parsed-
     * links on page are added to the url handler and content saved via the outStream.
     */
    @Override
    public void run() {
        makeRequestFromUrl(url);
        if (!sendRequest()) return;
        parseResponse();

        urlHandler.addToVisit(linksOnPage);
        synchronized (outStream) {
            try {
                outStream.write(url + ":" + content);
                outStream.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
