package com.chat;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends ConnectionHandler {

    private Server server;
    private String username;

    public ClientHandler(Server server, Socket socket) throws IOException {
        super(socket);
        this.server = server;
    }

    @Override
    public void abort() {
        synchronized (server.getClientHandlers()) {
            server.getClientHandlers().remove(this);
        }
        try {
            server.broadcastUsers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void handle(Object object) {
        Request request = (Request) object;
        System.out.println("New Request(type = " + request.getType() + ", text = " + request.getText() + ", username = " + username + ", roomId = " + request.getRoomId() + ")");

        if (username == null && request.getType() != Request.Type.SET_USERNAME)
            return;

        switch (request.getType()) {
            case SET_USERNAME:
                username = request.getText().strip();
                server.broadcastUser(username);
                try {
                    server.broadcastUsers();
                } catch (IOException e) {
                    return;
                }
                break;
            case CREATE_ROOM:
                Room room = Room.createRoom();
                room.getUsers().add(username);
                List<Room> rooms = server.getRooms();
                synchronized (rooms) {
                    rooms.add(room);
                }
                server.broadcastUser(username);
                break;
            case ADD_USER:
                rooms = server.getRooms();
                synchronized (rooms) {
                    for (Room r : rooms) {
                        if (r.getId() == request.getRoomId() && r.getUsers().contains(username) && !r.getUsers().contains(request.getText())) {
                            r.getUsers().add(request.getText());
                        }
                    }
                }
                try {
                    server.broadcastRoom(request.getRoomId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case REMOVE_USER:
                rooms = server.getRooms();
                synchronized (rooms) {
                    for (Room r : rooms) {
                        if (r.getId() == request.getRoomId() && r.getUsers().contains(username) && r.getUsers().contains(request.getText())) {
                            r.getUsers().remove(request.getText());
                        }
                    }
                }
                try {
                    server.broadcastRoom(request.getRoomId());
                    synchronized (server.getClientHandlers()) {
                        for (ClientHandler c : server.getClientHandlers()) {
                            if (c.getUsername() != null && c.getUsername().equals(request.getText()))
                                c.broadcast();
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case SEND_MESSAGE:
                rooms = server.getRooms();
                synchronized (rooms) {
                    for (Room r : rooms) {
                        if (r.getId() == request.getRoomId() && r.getUsers().contains(username)) {
                            r.getMessages().add(username + ": " + request.getText());
                        }
                    }
                }
                try {
                    server.broadcastRoom(request.getRoomId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
    }

    public void broadcast() {
        if (username == null)
            return;
        Response response = new Response(Response.Type.BROADCAST_ROOMS);
        List<Room> serverRooms = server.getRooms();
        synchronized (serverRooms) {
            for (Room r :serverRooms) {
                if (r.getUsers().contains(username)) {
                    response.getRooms().add(r);
                }
            }
        }
        try {
            send(response);
        } catch (IOException e) {
            return;
        }
    }
}
