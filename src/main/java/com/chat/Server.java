package com.chat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONWriter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Server implements Runnable {

    public static final String filePath = "/Users/kanstantinaspiatrashka/Desktop/data.json";
    private ServerSocket serverSocket = new ServerSocket(5555);
    private List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<ClientHandler>());
    private List<Room> rooms = Collections.synchronizedList(new ArrayList<Room>());

    private Thread serverThread = new Thread(this);

    public Server() throws IOException {
    }

    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler ch = new ClientHandler(this, socket);
                synchronized (clientHandlers) {
                    clientHandlers.add(ch);
                }
                ch.listen();
                broadcastUsers();
            } catch (IOException e) {
                return;
            }
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

    public void exportJSON() throws IOException {
        PrintWriter pw = new PrintWriter(filePath);
        JSONObject coreObject = new JSONObject();
        JSONArray jsonRooms = new JSONArray();
        for (Room r : rooms) {
            JSONObject jsonRoom = new JSONObject();
            JSONArray users = new JSONArray();
            for (String user : r.getUsers())
                users.put(user);
            jsonRoom.put("users", users);
            JSONArray messages = new JSONArray();
            for (String message : r.getMessages())
                messages.put(message);
            jsonRoom.put("messages", messages);
            jsonRoom.put("onlyTwoUsers", r.isOnlyTwoUsers());
            jsonRooms.put(jsonRoom);
        }
        coreObject.put("rooms", jsonRooms);
        pw.write(coreObject.toString());
        pw.close();
    }

    public void importJSON() {
        try {
            InputStream in = new FileInputStream(filePath);
            String s = new String(in.readAllBytes());
            JSONObject obj = new JSONObject(s);
            for (Object roomObj : obj.getJSONArray("rooms")) {
                JSONObject jsonRoom = (JSONObject) roomObj;
                Room room = Room.createRoom();
                for (Object str : jsonRoom.getJSONArray("users")) {
                    room.getUsers().add((String) str);
                }
                for (Object str : jsonRoom.getJSONArray("messages")) {
                    room.getMessages().add((String) str);
                }
                room.setOnlyTwoUsers(jsonRoom.getBoolean("onlyTwoUsers"));
                rooms.add(room);
            }
        } catch (IOException e) {
            return;
        }
    }

    public void start() {
        serverThread.start();
    }

    public void stop() throws IOException {
        serverSocket.close();
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
        server.importJSON();
        server.start();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.strip().equals("stop"))
                break;
            else if (line.strip().equals("save")) {
                server.stop();
                server.exportJSON();
                break;
            }
        }
        server.stop();
    }
}
