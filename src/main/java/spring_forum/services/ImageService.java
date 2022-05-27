package spring_forum.services;

import spring_forum.domain.enums.Gender;

import java.io.IOException;

public interface ImageService {

    byte[] getImage(String url, Gender gender);

    void uploadImage(String url, byte[] image) throws IOException;

    void deleteImage(String url);
}
