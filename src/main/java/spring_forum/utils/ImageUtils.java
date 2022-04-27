package spring_forum.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Slf4j
public class ImageUtils {

    private static byte[] defaultImage;

    public static byte[] getDefaultImage() {
        log.info("Getting default image");
        if (defaultImage == null) {
            try {
                URL resource = ImageUtils.class.getResource("/images/avatar_default.jpeg");
                File file = new File(resource.getPath());
                defaultImage = FileUtils.readFileToByteArray(file);
            } catch (IOException e) {
                log.warn("Cannot Load Default Image, message: " + e.getMessage());
            } catch (NullPointerException e) {
                log.warn("Cannot find default image.");
            }
        }
        return defaultImage;
    }
}
