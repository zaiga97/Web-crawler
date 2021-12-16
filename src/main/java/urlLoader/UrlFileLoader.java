/**
 * @autor Andrea Zasa
 * @since 09/12/2021
 */

package urlLoader;

import java.io.BufferedReader;
import java.io.FileReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class in an implementation of urlLoader for loading URLs from a text file.
 * URLs should be written in a text file and separated by lines.
 */
public class UrlFileLoader implements UrlLoader{
    private final String fileName;

    /**
     * @param fileName Name of the file from which load the URLs
     */
    public UrlFileLoader(String fileName){
        this.fileName = fileName;
    }

    /**
     * Load the URLs from the file.
     * Note that no check is done for asserting the goodness of the URLs
     * @return list of URLs on the file.
     */
    @Override
    public List<String> load() {
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader in = new BufferedReader (fileReader);
            List<String> urlList = new ArrayList<>();

            in.lines()
                    .forEach(l -> urlList.add(l));
            return urlList;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
