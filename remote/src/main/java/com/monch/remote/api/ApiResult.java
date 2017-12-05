package com.monch.remote.api;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 陈磊.
 */

public class ApiResult {

    private int code;
    private String message;
    private Map<String, Object> data;

    public static boolean isSuccess(ApiResult result) {
        return result != null && result.code == 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void put(String key, Object value) {
        if (data == null) {
            data = HttpUtils.getObjectMap();
        }
        data.put(key, value);
    }

    public <T> T get(String key) {
        if (data != null && data.containsKey(key)) {
            try {
                return (T) data.get(key);
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }

    public String getString(String key) {
        if (data != null && data.containsKey(key)) {
            try {
                return (String) data.get(key);
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }

    public Integer getInteger(String key) {
        if (data != null && data.containsKey(key)) {
            try {
                return (int) data.get(key);
            } catch (Exception e) {
                // ignore
            }
        }
        return 0;
    }

    public Long getLong(String key) {
        if (data != null && data.containsKey(key)) {
            try {
                return (long) data.get(key);
            } catch (Exception e) {
                // ignore
            }
        }
        return 0L;
    }

    public Float getFloat(String key) {
        if (data != null && data.containsKey(key)) {
            try {
                return (float) data.get(key);
            } catch (Exception e) {
                // ignore
            }
        }
        return 0f;
    }

    public Double getDouble(String key) {
        if (data != null && data.containsKey(key)) {
            try {
                return (double) data.get(key);
            } catch (Exception e) {
                // ignore
            }
        }
        return 0d;
    }

    public Boolean getBoolean(String key) {
        if (data != null && data.containsKey(key)) {
            try {
                return (boolean) data.get(key);
            } catch (Exception e) {
                // ignore
            }
        }
        return false;
    }

    public void release() {
        code = 0;
        message = null;
        HttpUtils.releaseObjectMap(data);
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
