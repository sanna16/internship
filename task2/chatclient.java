import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class ChatClient extends Application {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private PrintWriter out;
    private BufferedReader in;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chat Client");

        BorderPane root = new BorderPane();
        TextArea messageArea = new TextArea();
        messageArea.setEditable(false);
        TextField inputField = new TextField();
        Button sendButton = new Button("Send");

        root.setCenter(messageArea);
        root.setBottom(new BorderPane(inputField, null, sendButton, null, null));

        sendButton.setOnAction(e -> sendMessage(inputField.getText()));
        inputField.setOnAction(e -> sendMessage(inputField.getText()));

        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();

        new Thread(this::connectToServer).start();
    }

    private void connectToServer() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                final String message = serverMessage;
                Platform.runLater(() -> {
                    // Append message to the message area
                    // Assuming there's a reference to the message area (messageArea)
                    ((TextArea) ((BorderPane) ((Stage) getPrimaryStage()).getScene().getRoot()).getCenter()).appendText(message + "\n");
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        if (message != null && !message.trim().isEmpty()) {
            out.println(message);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
