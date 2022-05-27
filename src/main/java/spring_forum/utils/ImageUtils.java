package spring_forum.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import spring_forum.domain.enums.Gender;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Slf4j
public class ImageUtils {

    private static byte[] defaultImageMale;
    private static byte[] defaultImageFemale;

    public static byte[] getDefaultImage(Gender gender) {
        log.info("Getting default image");
        if (gender == Gender.F) {
            if (defaultImageFemale == null) {
                defaultImageFemale = loadDefaultImage(gender);
            }
            return defaultImageFemale;
        } else {
            if (defaultImageMale == null) {
                defaultImageMale = loadDefaultImage(gender);
            }
            return defaultImageMale;
        }
    }

    private static byte[] loadDefaultImage(Gender gender) {
        byte[] image = null;
        try {
            URL resource;
            if (gender == Gender.F) {
                resource = ImageUtils.class.getResource("/images/avatar_default_female.png");
            } else {
                resource = ImageUtils.class.getResource("/images/avatar_default_male.png");
            }
            File file = new File(resource.getPath());
            image = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            log.warn("Cannot Load Default Image, message: " + e.getMessage());
        } catch (NullPointerException e) {
            log.warn("Cannot find default image.");
        }
        return image;
    }
}
