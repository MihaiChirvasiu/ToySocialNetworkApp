package com.example.toysocialnetwork;

import com.example.toysocialnetwork.Domain.FriendRequest;
import com.example.toysocialnetwork.Domain.Friendship;
import com.example.toysocialnetwork.Domain.Message;
import com.example.toysocialnetwork.Domain.User;
import com.example.toysocialnetwork.Service.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class ControllerRegister {

    @FXML
    private ImageView shieldImageView;

    @FXML
    private TextField firstnameTextField;

    @FXML
    private TextField lastnameTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField setPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label confirmPasswordLabel;

    @FXML
    private Label registrationMessageLabel;

    @FXML
    private Button registerButton;

    @FXML
    private Button cancelButton;

    Controller<Long, User, Friendship, FriendRequest, Message> controller;
    Stage primaryStage;

    public void setController(Controller<Long, User, Friendship, FriendRequest, Message> controller, Stage stage) throws SQLException {
        this.controller = controller;
        this.primaryStage = stage;
    }

    public void registerButtonOnAction(ActionEvent event) throws NoSuchAlgorithmException, SQLException, IOException {
        if(!firstnameTextField.getText().isBlank()&&!lastnameTextField.getText().isBlank()&&!emailTextField.getText().isBlank()&&!setPasswordField.getText().isBlank()&&!confirmPasswordField.getText().isBlank())
        {
            if(controller.searchEmail(emailTextField.getText())==null)
            {
                if (setPasswordField.getText().equals(confirmPasswordField.getText())) {
                    String firstname = firstnameTextField.getText();
                    String lastname = lastnameTextField.getText();
                    String email = emailTextField.getText();
                    String password = setPasswordField.getText();
                    controller.addUser(firstname, lastname, email, password);
                    registrationMessageLabel.setText("User successfully registered!");
                    registrationMessageLabel.setTextFill(Color.BLUE);
                } else
                    confirmPasswordLabel.setText("Password does not match!");
            }
            else {
                registrationMessageLabel.setText("Email already registered!");
                registrationMessageLabel.setTextFill(Color.RED);
            }
        }
        else
        {
            registrationMessageLabel.setText("All fields must not be empty!");
            registrationMessageLabel.setTextFill(Color.RED);
        }
    }

    public void cancelButtonOnAction(ActionEvent event) throws IOException, SQLException {
        FXMLLoader userLoader = new FXMLLoader();
        userLoader.setLocation(getClass().getResource("login.fxml"));
        AnchorPane userTaskLayout = userLoader.load();
        primaryStage.setScene(new Scene(userTaskLayout));

        GUIController guiController = userLoader.getController();
        guiController.setController(controller,primaryStage);
    }

}
