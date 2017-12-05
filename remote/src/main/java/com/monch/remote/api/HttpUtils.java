package com.monch.remote.api;

import android.support.v4.util.Pools;
import android.text.TextUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 陈磊.
 */

public class HttpUtils {

    private static final int MAX_REQUEST_SIZE = 5;
    private static Pools.SimplePool<Map<String, String>> stringMapPools = new Pools.SimplePool<>(MAX_REQUEST_SIZE * 2);
    private static Pools.SimplePool<Map<String, File>> fileMapPools = new Pools.SimplePool<>(MAX_REQUEST_SIZE);
    private static Pools.SimplePool<Map<String, Object>> objectMapPools = new Pools.SimplePool<>(MAX_REQUEST_SIZE);

    static Map<String, String> getStringMap() {
        Map<String, String> map = stringMapPools.acquire();
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }

    static Map<String, File> getFileMap() {
        Map<String, File> map = fileMapPools.acquire();
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }

    static Map<String, Object> getObjectMap() {
        Map<String, Object> map = objectMapPools.acquire();
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }

    static void releaseStringMap(Map<String, String> map) {
        if (map != null) {
            map.clear();
            stringMapPools.release(map);
        }
    }

    static void releaseFileMap(Map<String, File> map) {
        if (map != null) {
            map.clear();
            fileMapPools.release(map);
        }
    }

    static void releaseObjectMap(Map<String, Object> map) {
        if (map != null) {
            map.clear();
            objectMapPools.release(map);
        }
    }

    static String encode(String string, Charset charset) {
        if (TextUtils.isEmpty(string)) return string;
        try {
            return URLEncoder.encode(string, charset.name());
        } catch (UnsupportedEncodingException e) {
            return string;
        }
    }

}
