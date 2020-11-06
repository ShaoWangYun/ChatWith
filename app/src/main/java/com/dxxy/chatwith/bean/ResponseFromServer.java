package com.dxxy.chatwith.bean;

public class ResponseFromServer {
    //状态码
    private int statusCode;
    //错误描述
    private String errorDescription;
    //请求返回的Json串（比如返回一个对象，或者是一个对象集合）
    private String responseJson;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getResponseJson() {
        return responseJson;
    }

    public void setResponseJson(String responseJson) {
        this.responseJson = responseJson;
    }

    public ResponseFromServer(int statusCode, String errorDescription, String responseJson) {
        this.statusCode = statusCode;
        this.errorDescription = errorDescription;
        this.responseJson = responseJson;
    }

    public ResponseFromServer() {
    }

    @Override
    public String toString() {
        return "ResponseFromServer{" +
                "statusCode=" + statusCode +
                ", errorDescription='" + errorDescription + '\'' +
                ", responseJson='" + responseJson + '\'' +
                '}';
    }
}
