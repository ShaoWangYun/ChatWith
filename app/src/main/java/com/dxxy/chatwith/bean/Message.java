package com.dxxy.chatwith.bean;
import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class Message extends LitePalSupport {

    private int id;
    private String flag;
    private String source;
    private String destination;
    private String message;
    private String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        flag = flag;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Message(int id, String flag, String source, String destination, String message, String date) {
        this.id = id;
        this.flag = flag;
        this.source = source;
        this.destination = destination;
        this.message = message;
        this.date = date;
    }

    public Message() {
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", flag='" + flag + '\'' +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
