package com.dxxy.chatwith.bean;

public class RequestFromClient {
    private String responseType;
    private String requestJson;

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }

    public RequestFromClient(String responseType, String requestJson) {
        this.responseType = responseType;
        this.requestJson = requestJson;
    }

    public RequestFromClient() {
    }

    @Override
    public String toString() {
        return "RequestFromClient{" +
                "responseType='" + responseType + '\'' +
                ", requestJson='" + requestJson + '\'' +
                '}';
    }
}
