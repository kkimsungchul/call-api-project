package com.main.api;

import java.util.HashMap;
import java.util.Map;

public class CallApiVO {
    private String method;
    private String url;
    private String jsonData;
    private Map<String, String> keyMap;

    private CallApiVO() {
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getJsonData() {
        return jsonData;
    }

    public Map<String, String> getKeyMap() {
        return keyMap;
    }

    public static class Builder {
        private String method;
        private String url;
        private String jsonData;
        private Map<String, String> keyMap;

        public Builder() {
            keyMap = new HashMap<>();
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder jsonData(String jsonData) {
            this.jsonData = jsonData;
            return this;
        }

        public Builder addKey(String key, String value) {
            this.keyMap.put(key, value);
            return this;
        }

        public CallApiVO build() {
            CallApiVO callApiVO = new CallApiVO();
            callApiVO.method = this.method;
            callApiVO.url = this.url;
            callApiVO.jsonData = this.jsonData;
            callApiVO.keyMap = this.keyMap;
            return callApiVO;
        }
    }

    @Override
    public String toString() {
        return "CallApiVO{" +
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", jsonData='" + jsonData + '\'' +
                ", keyMap=" + keyMap +
                '}';
    }
}
