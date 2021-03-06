package com.example.toysocialnetwork;

import com.example.toysocialnetwork.Domain.*;
import com.example.toysocialnetwork.Domain.Validators.ValidationException;
import com.example.toysocialnetwork.Repository.RepoException;
import com.example.toysocialnetwork.Service.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ControllerAddFriend {

    @FXML
    private TableView<User> tableViewAddFriend;

    @FXML
    private TableColumn<User, String> tableColumnFirstName;

    @FXML
    private TableColumn<User, String> tableColumnLastName;

    @FXML
    private TextField searchFriend;

    Controller<Long, User, Friendship, FriendRequest, Message, PublicEvent, GroupChat> controller;
    ObservableList<User> model = FXCollections.observableArrayList();
    Stage dialogStage;
    User friend;

    public void setService(Controller<Long, User, Friendship, FriendRequest, Message, PublicEvent, GroupChat> controller, Stage stage, User friend) throws SQLException {
        this.controller = controller;
        this.friend = friend;
        this.dialogStage = stage;
        initModel();
    }

    /**
     * Initialises the tableViewAddFriend
     */
    @FXML
    public void initialize(){
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("FirstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("LastName"));
        searchFriend.textProperty().addListener(o-> {
            try {
                handleFilter();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        tableViewAddFriend.setItems(model);
    }

    /**
     * Loads all the users
     * @throws SQLException database
     */
    private void initModel() throws SQLException {
        Set<Long> keysSet = controller.getKeysServ();
        List<User> users = new ArrayList<User>();
        for(Long k : keysSet){
            users.add(controller.findOneServ(k));
        }
        model.setAll(users);

    }

    /**
     * Filter for searching a User
     * @throws SQLException From Database
     */
    private void handleFilter() throws SQLException {
        Set<Long> keysSet = controller.getKeysServ();
        List<User> users = new ArrayList<User>();
        for(Long k : keysSet){
            users.add(controller.findOneServ(k));
        }
        Predicate<User> p1 = n -> n.getFirstName().toLowerCase().contains(searchFriend.getText().toLowerCase());
        Predicate<User> p2 = n -> n.getLastName().toLowerCase().contains(searchFriend.getText().toLowerCase());
        model.setAll(users.stream()
                .filter(p1.or(p2))
                .collect(Collectors.toList()));
    }

    /**
     * Button for the add request
     */
    public void handleAdd() {
        User selectedUser = tableViewAddFriend.getSelectionModel().getSelectedItem();
        if (selectedUser == null)
            MessageAlert.showErrorMessage(null, "No user selected!");
        else {
            try {
                controller.addFriendRequest(this.friend.getId(), selectedUser.getId());
            } catch (SQLException throwables) {
                MessageAlert.showErrorMessage(null, throwables.getMessage());
            } catch (ValidationException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            } catch (RepoException e) {
                MessageAlert.showErrorMessage(null, e.getErrorMessage());
            }
        }
    }

    /**
     * Button for going back a page
     * @throws IOException file
     * @throws SQLException database
     */
    @FXML
    public void goBack() throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("friendrequests-view.fxml"));

        AnchorPane root = (AnchorPane) loader.load();
        dialogStage.setTitle("FriendRequests");

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);

        ControllerDetails controllerDetails = loader.getController();
        controllerDetails.setService(controller, dialogStage, this.friend);
    }
}
