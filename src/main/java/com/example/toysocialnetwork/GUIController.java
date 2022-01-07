package com.example.toysocialnetwork;

import com.example.toysocialnetwork.Domain.FriendRequest;
import com.example.toysocialnetwork.Domain.Friendship;
import com.example.toysocialnetwork.Domain.Message;
import com.example.toysocialnetwork.Domain.User;
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

    Controller<Long, User, Friendship, FriendRequest, Message> controller;
    Stage primaryStage;
    /*@FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, String> tableColumnFirstName;

    @FXML
    private TableColumn<User, String> tableColumnLastName;*/

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

    public void setController(Controller<Long, User, Friendship, FriendRequest, Message> controller, Stage stage) throws SQLException {
        this.controller = controller;
        this.primaryStage=stage;
        //initModel();
    }

    /*@FXML
    public void initialize(){
        *//*tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("FirstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("LastName"));
        tableView.setItems(model);*//*
    }*/

    /**
     * Loads all the users
     * @throws SQLException
     */
    private void initModel() throws SQLException {
        Set<Long> keysSet = controller.getKeysServ();
        List<User> users = new ArrayList<User>();
        for(Long k : keysSet){
            users.add(controller.findOneServ(k));
        }

    }

    /**
     * Loads another window with details if a User is selected
     * @param ev
     * @throws IOException
     * @throws SQLException
     */
    @FXML
    public void handleFriendRequests(ActionEvent ev) throws IOException, SQLException {
        /*User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if(selectedUser != null)
            showUserFriendRequest(selectedUser);*/
    }

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


    public void validateLogin(String email, String password) throws SQLException, NoSuchAlgorithmException, IOException {
        if(controller.login(email,password)!=null)
        {
            User user = controller.login(email,password);
            showUserFriendRequest(user);
        }
        else
            loginMessageLabel.setText("Email or password invalid!");
    }

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
