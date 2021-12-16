import WebCrawler_AndreaZasa.PageCrawler;
import WebCrawler_AndreaZasa.UrlHandler;
import WebCrawler_AndreaZasa.WebCrawler;
import urlLoader.UrlFileLoader;

import java.io.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        //Initiate the IO interface
        BufferedWriter outStream = newWriter("SearchResults.txt");
        List<String> startingUrlList = new UrlFileLoader("src/main/resources/urlSites.txt").load();

        //Generate the new URLs handler
        UrlHandler urlHandler = new UrlHandler();
        urlHandler.addToVisit(startingUrlList);

        //Create a new webcrawler for handling all page requests
        WebCrawler webCrawler = new WebCrawler(4);

        //Crawl sites
        for (int i = 0; i < 100; i++) {
            webCrawler.add(new PageCrawler(urlHandler.nextRandom(), urlHandler, outStream));
            System.out.println(i);
        }

        //Shut down the webcrawler in a safe manner.
        webCrawler.shutdown(20);
        urlHandler.shutdown();

    }

    private static BufferedWriter newWriter(String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            return new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}



