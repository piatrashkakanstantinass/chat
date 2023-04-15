module com.chat.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;

    opens com.chat to javafx.fxml;
    exports com.chat;
}