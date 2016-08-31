package com.paxport.gcp.config.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class ConfigJsonUtils {

    private final static ObjectMapper mapper = ConfigJsonMapper.defaultObjectMapper();

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <E> E fromJson (String json, Class<E> targetType) {
        try {
            return mapper.readValue(json,targetType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encode(Object obj) {
        try {
            String json = mapper.writeValueAsString(obj);
            byte[] bytes = json.getBytes("UTF-8");
            return Base64.getEncoder().encodeToString(bytes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <E> E decode(String base64encoded, Class<E> target) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64encoded);
            String json = new String(bytes, "UTF-8");
            return mapper.readValue(json,target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
