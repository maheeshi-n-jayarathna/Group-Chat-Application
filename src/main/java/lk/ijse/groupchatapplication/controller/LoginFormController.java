package lk.ijse.groupchatapplication.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.ijse.groupchatapplication.ClientHandler;

import java.io.IOException;
import java.net.URL;

public class LoginFormController {
    @FXML
    public TextField txtUserName;

    @FXML
    protected void onHelloButtonClick() throws IOException {
        Stage stage = (Stage) txtUserName.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader();
        ChatFormController.userName = txtUserName.getText();

        stage.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/lk/ijse/groupchatapplication/view/ChatForm.fxml"))));
        stage.setOnCloseRequest(event -> {
//            ChatFormController.close();
            System.exit(0);
        });
        stage.show();
    }
}
