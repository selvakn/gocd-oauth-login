package org.gocd.plugin.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class JSONUtils {
    private static final Gson GSON = new GsonBuilder().create();

    public static <T> T fromJSON(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    public static Map<String, String> asMapOfStrings(String json) {
        return GSON.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
    }

    public static String toJSON(Object object) {
        return GSON.toJson(object);
    }
}
