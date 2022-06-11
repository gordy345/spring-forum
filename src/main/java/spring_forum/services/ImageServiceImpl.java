package spring_forum.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spring_forum.domain.enums.Gender;
import spring_forum.exceptions.NotFoundException;
import spring_forum.utils.Secret;

import java.io.IOException;

import static spring_forum.utils.ExceptionMessages.AVATAR_NOT_FOUND;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Value("${DEFAULT_AVATAR_MALE}")
    private String DEFAULT_AVATAR_MALE;

    @Value("${DEFAULT_AVATAR_FEMALE}")
    private String DEFAULT_AVATAR_FEMALE;

    @Override
    public byte[] getImage(String url, Gender gender) {
        log.info("Getting image with url: " + url);
        if (url == null) {
            if (gender == Gender.M) {
                return getImage(DEFAULT_AVATAR_MALE, gender);
            } else {
                return getImage(DEFAULT_AVATAR_FEMALE, gender);
            }
        }
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Authorization", "Basic " + Secret.getBase64());
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            byte[] bytes = IOUtils.toByteArray(entity.getContent());
            EntityUtils.consume(entity);
            return bytes;
        } catch (IOException e) {
            log.warn("Cannot load avatar image from disk with url: " + url);
            throw new NotFoundException(AVATAR_NOT_FOUND);
        }
    }

    @Override
    public void uploadImage(String url, byte[] image) throws IOException {
        log.info("Uploading image with url: " + url);
        HttpPut httpPut = new HttpPut(url);
        httpPut.setHeader("Authorization", "Basic " + Secret.getBase64());
        httpPut.setEntity(new ByteArrayEntity(image));
        httpClient.execute(httpPut);
    }

    @Override
    public void deleteImage(String url) {
        if (url == null) {
            return;
        }
        log.info("Deleting image with url: " + url);
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.setHeader("Authorization", "Basic " + Secret.getBase64());
        try {
            httpClient.execute(httpDelete);
        } catch (IOException e) {
            log.warn("Avatar wasn't deleted with url: " + url);
        }
    }
}
