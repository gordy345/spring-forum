package spring_forum.services;

import java.util.List;

public interface CacheService {
    String get(String key);

    byte[] getImage(String key);

    void put(String key, String value);

    void putImage(String key, byte[] image);

    boolean containsKey(String key);

    void remove(String... keys);

    void remove(List<String> keys);
}
