module com.chat.chat {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.chat to javafx.fxml;
    exports com.chat;
}