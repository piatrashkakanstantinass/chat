package com.chat;

import java.io.Serializable;

public class Request implements Serializable {
    public enum Type {
        SET_USERNAME,
        CREATE_ROOM,
        ADD_USER,
        REMOVE_USER,
        SEND_MESSAGE
    }

    private Type type;
    private String text;
    private int roomId;

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Request(Type type) {
        this.type = type;
    }

    public Request(Type type, String text) {
        this.type = type;
        this.text = text;
    }

    public Request(Type type, String text, int roomId) {
        this.type = type;
        this.text = text;
        this.roomId = roomId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
