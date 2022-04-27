package spring_forum.services;

import java.io.IOException;

public interface ImageService {

    byte[] getImage(String url);

    void uploadImage(String url, byte[] image) throws IOException;

    void deleteImage(String url);
}
