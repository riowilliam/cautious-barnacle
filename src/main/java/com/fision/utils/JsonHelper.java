package com.fision.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonHelper {
    public static String convertListToJsonString(List<?> list) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
