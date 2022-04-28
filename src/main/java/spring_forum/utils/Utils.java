package spring_forum.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Utils() {
    }

    public static <T> String convertToJson(T elem) {
        try {
            return OBJECT_MAPPER.writeValueAsString(elem);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot convert element to json, element: " + elem);
        }
    }
}
