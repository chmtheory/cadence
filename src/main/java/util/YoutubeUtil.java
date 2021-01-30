package util;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class YoutubeUtil {

    static {
        Logger logger = LoggerFactory.getLogger(YoutubeUtil.class);
        String token;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("youtube"));
            token = reader.readLine();
            logger.info("API key loaded successfully!");
        } catch (IOException ex) {
            token = "";
            logger.error("Unexpected exception while loading Youtube API key! YoutubeUtil will not function properly!", ex);
        }

        key = token;
        log = logger;
    }

    private final static String key;

    private final static Logger log;

    private YoutubeUtil() {

    }

    public static String extractYoutubeVideoIdFromMessage(String message) {
        Pattern p = Pattern.compile("https?://(?:www\\.)?(?:(?:m\\.)?youtube\\.com/watch\\?v=|youtu\\.be/)((?:[0-9a-zA-Z-_]){11})&?");
        Matcher m = p.matcher(message);

        if (m.find()) {
            return m.group(1);
        }

        return null;
    }


    public static String createYoutubeLinkFromId(String id) {
        return "https://youtu.be/" + id;
    }


    public static boolean doesVideoExist(String id) {
        // TODO: Potential optimization. Maybe cache?
        try {
            YouTube youtubeService = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    null).setApplicationName("Cadence").build();
            YouTube.Videos.List request = youtubeService.videos().list("id");
            VideoListResponse response = request.setPart("").setKey(key).setId(id).execute();
            return response.getItems().size() > 0;
        } catch (GeneralSecurityException | IOException ex) {
            log.error("Unexpected exception occurred!", ex);
            return false;
        }
    }

    public static String getVideoName(String id) {
        try {
            YouTube youtubeService = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    null).setApplicationName("Cadence").build();
            YouTube.Videos.List request = youtubeService.videos().list("id");
            VideoListResponse response = request.setPart("snippet").setKey(key).setId(id).execute();
            List<Video> items = response.getItems();
            if (items.size() == 0) {
                return null;
            }

            return items.get(0).getSnippet().getTitle();
        } catch (GeneralSecurityException | IOException ex) {
            log.error("Unexpected exception occurred!", ex);
            return null;
        }
    }


}
