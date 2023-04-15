package com.chat;

import java.io.Serializable;
import java.util.ArrayList;

public class Response implements Serializable {
    public enum Type {
        BROADCAST_ROOMS,
        BROADCAST_USERS
    }

    private Type type;
    private ArrayList<Room> rooms = new ArrayList<Room>();
    private ArrayList<String> users = new ArrayList<String>();

    public ArrayList<String> getUsers() {
        return users;
    }

    public Response(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }
}
