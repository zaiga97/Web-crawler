/**
 * @autor Andrea Zasa
 * @since 09/12/2021
 */

package urlLoader;
import java.util.List;

/**
 * Interface for loading a list of URLs
 */
public interface UrlLoader {
    public List<String> load();
}
