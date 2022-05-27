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
import org.springframework.stereotype.Service;
import spring_forum.domain.enums.Gender;
import spring_forum.utils.ImageUtils;
import spring_forum.utils.Secret;

import java.io.IOException;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Override
    public byte[] getImage(String url, Gender gender) {
        log.info("Getting image with url: " + url);
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Authorization", "Basic " + Secret.getBase64());
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            byte[] bytes = IOUtils.toByteArray(entity.getContent());
            EntityUtils.consume(entity);
            return bytes;
        } catch (IOException e) {
            log.warn("Cannot load avatar image from disk, setting default..");
            return ImageUtils.getDefaultImage(gender);
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
        } catch (IOException ignored) {
        }
    }
}
