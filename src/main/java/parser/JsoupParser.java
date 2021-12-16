/**
 * @autor Andrea Zasa
 * @since 09/12/2021
 */
package parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is used for parsing a Http response and extract main content of the webpage and links on it.
 */
public class JsoupParser implements Parser{

    private Document doc;

    /**
     * Provided you have already used parse(). It will return the links on the page
     * @return list of links on page
     */
    @Override
    public List<String> getLinks() {
        List<String> links = new LinkedList<>();

        Elements linksElem = doc.select("a[href]");
        linksElem.stream()
                .filter(s -> s.absUrl("href").startsWith("http"))
                .forEach(s -> links.add(s.absUrl("href")));

        return  links;
    }

    /**
     * Provided you have already used parse(). It will return the main content of the page as text.
     * @return main content on the page.
     */
    @Override
    public String getContent() {
        return doc.body().text();
    }

    /**
     * First method to invoke of the class. Given a http response in String format parse it and store the content
     * in a hidden variable. Further methods can then be invoked to return meaningful part of the page.
     * @param response a Http response to parse.
     */
    @Override
    public void parse(String response) {
        doc = Jsoup.parse(response);
    }
}
