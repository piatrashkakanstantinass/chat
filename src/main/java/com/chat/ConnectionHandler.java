package com.chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class ConnectionHandler {
    private Socket socket;
    private ObjectOutputStream out;

    public ConnectionHandler(Socket socket) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    public void listen() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    while (true) {
                        Object object = in.readObject();
                        handle(object);
                    }

                } catch (IOException e) {
                    abort();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    public void stop() throws IOException {
        socket.close();
    }

    public void send(Object object) throws IOException {
        out.writeObject(object);
        out.reset();
    }

    public abstract void abort();
    public abstract void handle(Object object);

}
