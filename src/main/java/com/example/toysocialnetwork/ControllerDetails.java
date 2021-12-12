package com.example.toysocialnetwork;

import com.example.toysocialnetwork.Domain.FriendRequest;
import com.example.toysocialnetwork.Domain.Friendship;
import com.example.toysocialnetwork.Domain.Message;
import com.example.toysocialnetwork.Domain.User;
import com.example.toysocialnetwork.Service.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ControllerDetails {

    @FXML
    private TableView<User> tableViewFriends;

    @FXML
    private TableView<User> tableViewRequests;

    @FXML
    private TableColumn<User, String> tableColumnFirstName;

    @FXML
    private TableColumn<User, String> tableColumnLastName;

    @FXML
    private TableColumn<User, String> tableColumnFirstNameRequest;

    @FXML
    private TableColumn<User, String> tableColumnLastNameRequest;

    @FXML
    private TableColumn<FriendRequest, String> tableColumnStatus;

    @FXML
    private TableColumn<FriendRequest, String> tableColumnDate;

    @FXML
    private TextField searchFriend;

    @FXML
    private Button addFriend;

    @FXML
    private Button deleteFriend;

    @FXML
    private Button acceptFriendRequest;

    @FXML
    private Button rejectFriendRequest;

    Controller<Long, User, Friendship, FriendRequest, Message> controller;
    ObservableList<User> model = FXCollections.observableArrayList();
    Stage detailStage;
    User friend;

    public void setService(Controller<Long, User, Friendship, FriendRequest, Message> controller, Stage stage, User user) throws SQLException {
        this.controller = controller;
        this.detailStage = stage;
        this.friend = user;
        initModel(user);
    }

    @FXML
    public void deleteFriend() throws SQLException, IOException {
        User selectedUser = tableViewFriends.getSelectionModel().getSelectedItem();
        this.controller.deleteFriendshipServ(this.friend.getId(), selectedUser.getId());
        this.controller.deleteFriendshipServ(selectedUser.getId(), this.friend.getId());
    }

    @FXML
    public void addFriend() throws SQLException, IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addfriend-view.fxml"));

        AnchorPane root = (AnchorPane) loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("AddFriend");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        //dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);

        ControllerAddFriend controllerDetails = loader.getController();
        controllerDetails.setService(controller, dialogStage, this.friend);

        dialogStage.show();
    }

    @FXML
    public void initialize(){
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("FirstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("LastName"));
        tableViewFriends.setItems(model);
    }

    private void initModel(User user) throws SQLException {
        List<User> secondUsers = new ArrayList<User>();
        List<Friendship> friendships = controller.findFriendshipsForUser(user.getId());
        for(int i = 0; i < friendships.size(); i++){
            secondUsers.add(friendships.get(i).getSecondUser());
        }
        model.setAll(secondUsers);

    }

}
