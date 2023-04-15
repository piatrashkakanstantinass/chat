package com.chat;

import javafx.application.Platform;

import java.io.IOException;
import java.net.Socket;

public class ServerHandler extends ConnectionHandler {
    private Client client;
    public ServerHandler(Client client, Socket socket) throws IOException {
        super(socket);
        this.client = client;
    }

    @Override
    public void abort() {

    }

    @Override
    public void handle(Object object) {
        Response response = (Response) object;
        System.out.println("New Response(type = " + response.getType() + ", rooms = {");
        for (Room r : response.getRooms()) {
            System.out.print("\tRoom(id = " + r.getId() + ", users = ");
            for (String user : r.getUsers())
                System.out.print(user + ", ");
            System.out.println("messages = {");
            for (String m : r.getMessages()) {
                System.out.println("\t\t" + m + ",");
            }
            System.out.println("\t}),");
        }
        System.out.println("})");

        switch (response.getType()) {
            case BROADCAST_ROOMS:
                client.setRooms(response.getRooms());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (client.getController() == null)
                            return;
                        Room selectedRoom = client.getController().roomComboBox.getValue();
                        client.getController().roomComboBox.getItems().clear();
                        client.getController().roomComboBox.getItems().addAll(response.getRooms());
                        if (selectedRoom == null)
                            return;
                        for (Room r : client.getController().roomComboBox.getItems()) {
                            if (r.getId() == selectedRoom.getId()) {
                                client.getController().roomComboBox.getSelectionModel().select(r);
                            }
                        }
                    }
                });
                break;
            case BROADCAST_USERS:
                if (client.getController() == null)
                    return;
                client.getController().usersArea.clear();
                String users = "";
                for (String user : response.getUsers())
                    users += user + "\n";
                client.getController().usersArea.setText(users);
                break;
        }
    }
}
