package com.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    private ServerSocket serverSocket = new ServerSocket(5555);
    private List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<ClientHandler>());
    private List<Room> rooms = Collections.synchronizedList(new ArrayList<Room>());

    public Server() throws IOException {
    }

    public void run() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            ClientHandler ch = new ClientHandler(this, socket);
            synchronized (clientHandlers) {
                clientHandlers.add(ch);
            }
            ch.listen();
            broadcastUsers();
        }
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public void broadcastRoom(int roomId) throws IOException {
        synchronized (rooms) {
            for (Room r : rooms) {
                if (r.getId() == roomId) {
                    synchronized (clientHandlers) {
                        for (ClientHandler ch : clientHandlers) {
                            if (ch.getUsername() != null && r.getUsers().contains(ch.getUsername())) {
                                ch.broadcast();
                            }
                        }

                    }
                }
            }
        }
    }

    public void broadcastUsers() throws IOException {
        synchronized (clientHandlers) {
            Response response = new Response(Response.Type.BROADCAST_USERS);
            for (ClientHandler c : clientHandlers) {
                if (c.getUsername() != null)
                    response.getUsers().add(c.getUsername());
            }
            for (ClientHandler c : clientHandlers) {
                c.send(response);
            }
        }
    }

    public void broadcastUser(String user) {
        synchronized (clientHandlers) {
            for (ClientHandler c : clientHandlers) {
                if (c.getUsername().equals(user))
                    c.broadcast();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.run();
    }
}
