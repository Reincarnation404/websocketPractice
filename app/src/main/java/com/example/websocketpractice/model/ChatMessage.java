package com.example.websocketpractice.model;

import androidx.annotation.NonNull;

public class ChatMessage {
    private String content;
    private String time;
    private int isMeSend;//0是對方發送 1是自己發送
    private int isRead;//是否已讀(0未讀 1已讀)

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIsMeSend() {
        return isMeSend;
    }

    public void setIsMeSend(int isMeSend) {
        this.isMeSend = isMeSend;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatMessage{" +
                "content=" + content +
                ", time='" + time + '\'' +
                ", isMeSend='" + isMeSend + '\'' +
                ", isRead='" + isRead +
                '}';
    }
}
