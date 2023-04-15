package com.chat;

import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable {

    private int id;
    private ArrayList<String> users = new ArrayList<String>();
    private ArrayList<String> messages = new ArrayList<String>();

    private static int nextId = 0;

    private boolean onlyTwoUsers = false;

    public boolean isOnlyTwoUsers() {
        return onlyTwoUsers;
    }

    public void setOnlyTwoUsers(boolean onlyTwoUsers) {
        this.onlyTwoUsers = onlyTwoUsers;
    }

    public static Room createRoom() {
        return new Room();
    }
    private Room() {
        this.id = nextId++;
    }

    public int getId() {
        return id;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }
}
