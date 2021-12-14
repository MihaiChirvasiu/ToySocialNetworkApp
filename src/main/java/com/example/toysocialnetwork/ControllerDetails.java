package com.example.toysocialnetwork;

import com.example.toysocialnetwork.Domain.*;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ControllerDetails {

    @FXML
    private TableView<User> tableViewFriends;

    @FXML
    private TableView<FriendRequestDTO> tableViewRequestsSent;

    @FXML
    private TableView<FriendRequestDTO> tableViewRequestsReceived;

    @FXML
    private TableColumn<User, String> tableColumnFirstName;

    @FXML
    private TableColumn<User, String> tableColumnLastName;

    @FXML
    private TableColumn<FriendRequestDTO, String> tableColumnFirstNameRequestSent;

    @FXML
    private TableColumn<FriendRequestDTO, String> tableColumnLastNameRequestSent;

    @FXML
    private TableColumn<FriendRequestDTO, String> tableColumnStatusSent;

    @FXML
    private TableColumn<FriendRequestDTO, String> tableColumnDateSent;

    @FXML
    private TableColumn<FriendRequestDTO, String> tableColumnFirstNameRequestReceived;

    @FXML
    private TableColumn<FriendRequestDTO, String> tableColumnLastNameRequestReceived;

    @FXML
    private TableColumn<FriendRequestDTO, String> tableColumnStatusReceived;

    @FXML
    private TableColumn<FriendRequestDTO, String> tableColumnDateReceived;

    @FXML
    private TextField searchFriend;

    @FXML
    private TextField searchRequestReceived;

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
    ObservableList<FriendRequestDTO> modelRequestSent = FXCollections.observableArrayList();
    ObservableList<FriendRequestDTO> modelRequestReceived = FXCollections.observableArrayList();
    Stage detailStage;
    User friend;

    public void setService(Controller<Long, User, Friendship, FriendRequest, Message> controller, Stage stage, User user) throws SQLException {
        this.controller = controller;
        this.detailStage = stage;
        this.friend = user;
        initModel(user);
        initModelRequestSent(user);
        initModelRequestReceived(user);
    }

    @FXML
    public void acceptFriendRequest() throws SQLException, IOException {
        FriendRequestDTO selectedUser = tableViewRequestsReceived.getSelectionModel().getSelectedItem();
        if(selectedUser == null)
            MessageAlert.showErrorMessage(null, "No user selected!");
        else
            this.controller.acceptFriendRequest(selectedUser.getId(), this.friend.getId());
    }

    @FXML
    public void rejectFriendRequest() throws SQLException, IOException {
        FriendRequestDTO selectedUser = tableViewRequestsReceived.getSelectionModel().getSelectedItem();
        if(selectedUser == null)
            MessageAlert.showErrorMessage(null, "No user selected!");
        else
            this.controller.rejectFriendRequest(selectedUser.getId(), this.friend.getId());
    }

    @FXML
    public void deleteFriend() throws SQLException, IOException {
        User selectedUser = tableViewFriends.getSelectionModel().getSelectedItem();
        if(selectedUser == null)
            MessageAlert.showErrorMessage(null, "No user selected!");
        else {
            this.controller.deleteFriendshipServ(this.friend.getId(), selectedUser.getId());
            this.controller.deleteFriendshipServ(selectedUser.getId(), this.friend.getId());
        }
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
        tableColumnFirstNameRequestSent.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("FirstName"));
        tableColumnLastNameRequestSent.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("LastName"));
        tableColumnStatusSent.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("Status"));
        tableColumnDateSent.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("Date"));
        tableViewRequestsSent.setItems(modelRequestSent);
        tableColumnFirstNameRequestReceived.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("FirstName"));
        tableColumnLastNameRequestReceived.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("LastName"));
        tableColumnStatusReceived.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("Status"));
        tableColumnDateReceived.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("Date"));
        tableViewRequestsReceived.setItems(modelRequestReceived);
        searchRequestReceived.textProperty().addListener(o-> {
            try {
                handleFilterReceived();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        searchFriend.textProperty().addListener(o-> {
            try {
                handleFilter();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void initModel(User user) throws SQLException {
        List<User> secondUsers = new ArrayList<User>();
        List<Friendship> friendships = controller.findFriendshipsForUser(user.getId());
        for(int i = 0; i < friendships.size(); i++){
            secondUsers.add(friendships.get(i).getSecondUser());
        }
        model.setAll(secondUsers);

    }

    private void initModelRequestSent(User user) throws SQLException
    {
        List<FriendRequestDTO> secondUsers = new ArrayList<FriendRequestDTO>();
        List<FriendRequest> friendRequests = controller.findFriendRequestsForUser(user.getId());
        for(int i = 0; i < friendRequests.size(); i++){
            FriendRequestDTO friendRequestDTO=new FriendRequestDTO(friendRequests.get(i));
            secondUsers.add(friendRequestDTO);
        }
        modelRequestSent.setAll(secondUsers);
    }

    private void initModelRequestReceived(User user) throws SQLException
    {
        List<FriendRequestDTO> secondUsers = new ArrayList<FriendRequestDTO>();
        List<FriendRequest> friendRequests = controller.findFriendRequestsForUserReceived(user.getId());
        for(int i = 0; i < friendRequests.size(); i++){
            FriendRequestDTO friendRequestDTO=new FriendRequestDTO(friendRequests.get(i));
            secondUsers.add(friendRequestDTO);
        }
        modelRequestReceived.setAll(secondUsers);
    }

    private void handleFilterReceived() throws SQLException {
        List<FriendRequestDTO> users = new ArrayList<FriendRequestDTO>();
        List<FriendRequest> friendRequests = controller.findFriendRequestsForUserReceived(this.friend.getId());
        for(int i = 0; i < friendRequests.size(); i++){
            FriendRequestDTO friendRequestDTO=new FriendRequestDTO(friendRequests.get(i));
            users.add(friendRequestDTO);
        }
        Predicate<FriendRequestDTO> p1 = n -> n.getFirstName().toLowerCase().contains(searchRequestReceived.getText().toLowerCase());
        Predicate<FriendRequestDTO> p2 = n -> n.getLastName().toLowerCase().contains(searchRequestReceived.getText().toLowerCase());
        modelRequestReceived.setAll(users.stream()
                .filter(p1.or(p2))
                .collect(Collectors.toList()));
    }

    private void handleFilter() throws SQLException {
        List<User> users = new ArrayList<User>();
        List<Friendship> friendships= controller.findFriendshipsForUser(this.friend.getId());
        for(int i=0;i<friendships.size();i++)
            users.add(friendships.get(i).getSecondUser());
        Predicate<User> p1 = n -> n.getFirstName().toLowerCase().contains(searchFriend.getText().toLowerCase());
        Predicate<User> p2 = n -> n.getLastName().toLowerCase().contains(searchFriend.getText().toLowerCase());
        model.setAll(users.stream()
                .filter(p1.or(p2))
                .collect(Collectors.toList()));
    }

}
