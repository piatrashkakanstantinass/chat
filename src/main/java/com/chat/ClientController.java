package com.chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientController {
    private Client client;

    @FXML
    ComboBox<Room> roomComboBox;

    @FXML
    TextArea chatArea, usersArea;

    @FXML
    TextField messageField;

    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    void createRoom(ActionEvent event) throws IOException {
        client.getServerHandler().send(new Request(Request.Type.CREATE_ROOM));
    }

    @FXML
    void send(ActionEvent event) throws IOException {
        Room currentRoom = roomComboBox.getValue();
        String msg = messageField.getText();
        messageField.clear();
        if (currentRoom == null)
            return;
        if (msg.startsWith("\\addUser")) {
            try {
                String user = msg.split("\s", 2)[1];
                client.getServerHandler().send(new Request(Request.Type.ADD_USER, user, currentRoom.getId()));
            } catch (Exception e) {
                return;
            }
        }
        else if (msg.startsWith("\\removeUser")) {
            try {
                String user = msg.split("\s", 2)[1];
                client.getServerHandler().send(new Request(Request.Type.REMOVE_USER, user, currentRoom.getId()));
            } catch (Exception e) {
                return;
            }
        }
        else {
            client.getServerHandler().send(new Request(Request.Type.SEND_MESSAGE, msg, currentRoom.getId()));
        }
    }

    @FXML
    void setRoom(ActionEvent event) {
        chatArea.clear();
        if (roomComboBox.getSelectionModel().getSelectedIndex() < 0)
            return;
        Room r = roomComboBox.getValue();
        String messages = "";
        for (String msg : r.getMessages()) {
            messages += msg + "\n";
        }
        chatArea.setText(messages);
    }
}
