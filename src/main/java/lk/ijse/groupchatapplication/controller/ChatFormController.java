package lk.ijse.groupchatapplication.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class ChatFormController implements Initializable {
    @FXML
    public AnchorPane ancPane;
    @FXML
    public ScrollPane scPane;
    @FXML
    public TextField txtMessage;
    @FXML
    public VBox vBox;
    public static String userName;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public void close() {
        try {
            String mes = ChatFormController.userName + " has left the chat ";
            dataOutputStream.writeBoolean(false);
            dataOutputStream.writeUTF(mes);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.socket = new Socket("localhost", 1235);
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeBoolean(false);
            dataOutputStream.writeUTF(userName);
            dataOutputStream.flush();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (socket.isConnected()){
                        try {
//                            String message = dataInputStream.readUTF();
                            if (dataInputStream.readBoolean()){
                                receivingImage();
                            }else {
                                receivingTextMessage(dataInputStream.readUTF());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receivingTextMessage(String message) {
        HBox hBox = new HBox();
        hBox.setStyle("-fx-alignment: center-left;-fx-fill-height: true;-fx-min-height: 50;-fx-pref-width: 520;-fx-max-width: 520;-fx-padding: 10");
        Label messageLbl = new Label(message);
        messageLbl.setStyle("-fx-background-color:   #2980b9;-fx-background-radius:15;-fx-font-size: 18;-fx-font-weight: normal;-fx-text-fill: white;-fx-wrap-text: true;-fx-alignment: center-left;-fx-content-display: left;-fx-padding: 10;-fx-max-width: 350;");
        hBox.getChildren().add(messageLbl);
        Platform.runLater(() -> vBox.getChildren().add(hBox));
    }

    private void receivingImage() {
        try {
//            String utf = dataInputStream.readUTF();
            int size = dataInputStream.readInt();
            byte[] bytes = new byte[size];
            dataInputStream.readFully(bytes);
            System.out.println(userName + "- Image received: from ");

            HBox hBox = new HBox();
            Label messageLbl = new Label(userName);
            messageLbl.setStyle("-fx-background-color: #2980b9;-fx-background-radius:15;-fx-font-size: 18;-fx-font-weight: normal;-fx-text-fill: white;-fx-wrap-text: true;-fx-alignment: center;-fx-content-display: left;-fx-padding: 10;-fx-max-width: 350;");
            hBox.setStyle("-fx-fill-height: true; -fx-min-height: 50; -fx-pref-width: 520; -fx-max-width: 520; -fx-padding: 10; -fx-alignment: center-right;");

            Platform.runLater(()->{
                ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(bytes)));
                imageView.setStyle("-fx-padding: 5px;");
                imageView.setFitHeight(200);
                imageView.setFitWidth(120);

                hBox.getChildren().addAll(messageLbl, imageView);

                vBox.getChildren().add(hBox);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void messageSendOnAction(ActionEvent actionEvent) {
        String textMessage = txtMessage.getText();
        sendTextMessage(textMessage);
    }

    private void sendTextMessage(String textMessage) {
        try {
            if (textMessage != null){
                HBox hBox = new HBox();
                hBox.setStyle("-fx-alignment: center-right;-fx-fill-height: true;-fx-min-height: 50;-fx-pref-width: 520;-fx-max-width: 520;-fx-padding: 10");
                Label messageLbl = new Label(textMessage);
                messageLbl.setStyle("-fx-background-color:  purple;-fx-background-radius:15;-fx-font-size: 18;-fx-font-weight: normal;-fx-text-fill: white;-fx-wrap-text: true;-fx-alignment: center-left;-fx-content-display: left;-fx-padding: 10;-fx-max-width: 350;");
                hBox.getChildren().add(messageLbl);

                vBox.getChildren().add(hBox);

                dataOutputStream.writeBoolean(false);
                dataOutputStream.writeUTF(textMessage);
                dataOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void imgSendOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image ");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(imageFilter);
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            try {
                byte[] bytes = Files.readAllBytes(selectedFile.toPath());
                HBox hBox = new HBox();
                hBox.setStyle("-fx-fill-height: true; -fx-min-height: 50; -fx-pref-width: 520; -fx-max-width: 520; -fx-padding: 10; -fx-alignment: center-right;");

                // Display the image in an ImageView or any other UI component
                ImageView imageView = new ImageView(new Image(new FileInputStream(selectedFile)));

                imageView.setStyle("-fx-padding: 10px;");
                imageView.setFitHeight(180);
                imageView.setFitWidth(100);

                hBox.getChildren().addAll(imageView);
                vBox.getChildren().add(hBox);

//                dataOutputStream.writeUTF("%*%image%*%");
                dataOutputStream.writeBoolean(true);
                dataOutputStream.writeInt(bytes.length);
                dataOutputStream.write(bytes);
                dataOutputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
