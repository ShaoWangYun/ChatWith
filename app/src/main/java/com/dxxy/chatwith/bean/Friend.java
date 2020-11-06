package com.dxxy.chatwith.bean;

public class Friend {
    private String myName;
    private String friendName;
    private int friendStatus;

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public int getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(int friendStatus) {
        this.friendStatus = friendStatus;
    }

    public Friend(String myName, String friendName, int friendStatus) {
        this.myName = myName;
        this.friendName = friendName;
        this.friendStatus = friendStatus;
    }

    public Friend() {
    }

    @Override
    public String toString() {
        return "Friend{" +
                "myName='" + myName + '\'' +
                ", friendName='" + friendName + '\'' +
                ", friendStatus=" + friendStatus +
                '}';
    }
}
