package com.example.toysocialnetwork;

import com.example.toysocialnetwork.Domain.*;
import com.example.toysocialnetwork.Events.EntityChangeEvent;
import com.example.toysocialnetwork.Observer.Observer;
import com.example.toysocialnetwork.Service.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GUIController {

    Controller<Long, User, Friendship, FriendRequest, Message, PublicEvent, GroupChat> controller;
    Stage primaryStage;

    @FXML
    private Button cancelButton;

    @FXML
    private Label loginMessageLabel;

    @FXML
    private ImageView brandingImageView;

    @FXML
    private ImageView lockImageView;

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField enterPasswordField;

    public void setController(Controller<Long, User, Friendship, FriendRequest, Message, PublicEvent, GroupChat> controller, Stage stage) throws SQLException {
        this.controller = controller;
        this.primaryStage=stage;
    }


    /**
     * Initialises the controllerDetails
     * @param user the user for whom to load the controller
     * @throws IOException file
     * @throws SQLException database
     */
    public void showUserFriendRequest(User user) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("friendrequests-view.fxml"));

        AnchorPane root = (AnchorPane) loader.load();

        primaryStage.setTitle("FriendRequests");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        ControllerDetails controllerDetails = loader.getController();
        controllerDetails.setService(controller, primaryStage, user);


    }

    /**
     * Function handler for the login Button
     * @param event
     * @throws SQLException database
     * @throws NoSuchAlgorithmException hash
     * @throws IOException file
     */
    public void loginButtonOnAction(ActionEvent event) throws SQLException, NoSuchAlgorithmException, IOException {
        if(emailTextField.getText().isBlank() == false && enterPasswordField.getText().isBlank() == false)
        {
            validateLogin(emailTextField.getText(),enterPasswordField.getText());
        }
        else
        {
            loginMessageLabel.setText("Please enter username and password!");
        }
    }

    /**
     * We need to check that the given data corresponds with the database credentials
     * @param email the email given
     * @param password the password given
     * @throws SQLException database
     * @throws NoSuchAlgorithmException hash
     * @throws IOException file
     */
    public void validateLogin(String email, String password) throws SQLException, NoSuchAlgorithmException, IOException {
        if(controller.login(email,password)!=null)
        {
            User user = controller.login(email,password);
            showUserFriendRequest(user);
        }
        else
            loginMessageLabel.setText("Email or password invalid!");
    }

    /**
     * Initialises the register Controller
     */
    public void createAccountForm()
    {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("register.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            ControllerRegister controllerDetails = loader.getController();
            controllerDetails.setController(controller, primaryStage);
        }catch (Exception e)
        {
            e.printStackTrace();
            e.getCause();
        }
    }

}
