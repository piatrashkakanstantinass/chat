package com.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;

public class Client extends Application {
    private Socket socket = new Socket("localhost", 5555);
    private ServerHandler serverHandler = new ServerHandler(this, socket);
    private ArrayList<Room> rooms = new ArrayList<Room>();

    private ClientController controller;

    public Client() throws IOException {
        serverHandler.listen();
    }

    public ServerHandler getServerHandler() {
        return serverHandler;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public ClientController getController() {
        return controller;
    }

    public void setController(ClientController controller) {
        this.controller = controller;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setOnCloseRequest(event -> {
            try {
                getServerHandler().stop();
            } catch (IOException e) {
                return;
            }
        });
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Username");
        Optional<String> result;
        do {
            result = dialog.showAndWait();
        } while (!result.isPresent() && result.get().strip().length() > 0);
        String username = result.get().strip();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view.fxml"));
        Scene scene = new Scene(loader.load());
        controller = loader.getController();
        controller.setClient(this);
        serverHandler.send(new Request(Request.Type.SET_USERNAME, username));
        stage.setScene(scene);
        stage.show();
    }

    public void start() {
        launch();
    }
}
