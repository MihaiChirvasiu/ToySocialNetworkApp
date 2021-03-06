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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ControllerDetails implements Observer<EntityChangeEvent> {

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

    @FXML
    private Button cancelRequest;

    @FXML
    private Button ChatButton;

    @FXML
    private Button eventButton;

    @FXML
    private Button backButton;

    @FXML
    private ComboBox<LocalDate> startDate;

    @FXML
    private ComboBox<LocalDate> endDate;

    @FXML
    private Button reportActivities;

    @FXML
    private Button report;

    Controller<Long, User, Friendship, FriendRequest, Message, PublicEvent, GroupChat> controller;
    ObservableList<User> model = FXCollections.observableArrayList();
    ObservableList<FriendRequestDTO> modelRequestSent = FXCollections.observableArrayList();
    ObservableList<FriendRequestDTO> modelRequestReceived = FXCollections.observableArrayList();
    Stage detailStage;
    User friend;

    public void setService(Controller<Long, User, Friendship, FriendRequest, Message, PublicEvent, GroupChat> controller, Stage stage, User user) throws SQLException {
        this.controller = controller;
        this.detailStage = stage;
        this.friend = user;
        initModel(user);
        initModelRequestSent(user);
        initModelRequestReceived(user);
        loadCombos();
    }

    /**
     * Function for loading the comboBoxes used
     */
    private void loadCombos(){
        ObservableList<LocalDate> dates = FXCollections.observableArrayList();
        LocalDate start = LocalDate.parse("2021-01-01");
        LocalDate end = LocalDate.now();
        do{
            dates.add(start);
            start = start.plusMonths(1);
        }while (start.isBefore(end));
        dates.add(start);
        startDate.setItems(dates);
        endDate.setItems(dates);
    }

    /**
     * Function handler for choosing the period for the activities report
     * @throws SQLException database
     * @throws IOException file
     */
    @FXML
    public void queryActivities() throws SQLException, IOException {
        LocalDate start = startDate.getSelectionModel().getSelectedItem();
        LocalDate end = endDate.getSelectionModel().getSelectedItem();
        if(start == null || end == null)
            MessageAlert.showErrorMessage(null, "No date selected!");
        else
            controller.queryFriend(friend.getId(), start, end);
    }



    /**
     * Accepts a friendRequest, or shows an error message if no User is selected
     * @throws SQLException From database
     * @throws IOException
     */
    @FXML
    public void acceptFriendRequest() throws SQLException, IOException {
        FriendRequestDTO selectedUser = tableViewRequestsReceived.getSelectionModel().getSelectedItem();
        if(selectedUser == null)
            MessageAlert.showErrorMessage(null, "No user selected!");
        else
            this.controller.acceptFriendRequest(selectedUser.getId(), this.friend.getId());
    }

    /**
     * Rejects a friendRequest, or shows an error message if no User is selected
     * @throws SQLException
     * @throws IOException
     */
    @FXML
    public void rejectFriendRequest() throws SQLException, IOException {
        FriendRequestDTO selectedUser = tableViewRequestsReceived.getSelectionModel().getSelectedItem();
        if(selectedUser == null)
            MessageAlert.showErrorMessage(null, "No user selected!");
        else
            this.controller.rejectFriendRequest(selectedUser.getId(), this.friend.getId());
    }

    /**
     * Deletes a friend or shows an error message if no user is selected
     * @throws SQLException
     * @throws IOException
     */
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

    /**
     * Initialises the addFriend Controller
     * @throws SQLException database
     * @throws IOException file
     */
    @FXML
    public void addFriend() throws SQLException, IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addfriend-view.fxml"));

        AnchorPane root = (AnchorPane) loader.load();
        detailStage.setTitle("AddFriend");

        Scene scene = new Scene(root);
        detailStage.setScene(scene);

        ControllerAddFriend controllerDetails = loader.getController();
        controllerDetails.setService(controller, detailStage, this.friend);
    }

    /**
     * Initialises the eventController
     * @throws SQLException database
     * @throws IOException file
     */
    @FXML
    public void events() throws SQLException, IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("event.fxml"));

        AnchorPane root = (AnchorPane) loader.load();
        detailStage.setTitle("Events");

        Scene scene = new Scene(root);
        detailStage.setScene(scene);

        ControllerEvent controllerDetails = loader.getController();
        controllerDetails.start();
        controllerDetails.setService(controller, detailStage, this.friend);
    }

    /**
     * Function handler for going back a page
     * @throws IOException file
     * @throws SQLException database
     */
    @FXML
    public void goBack() throws IOException, SQLException {
        FXMLLoader userLoader = new FXMLLoader();
        userLoader.setLocation(getClass().getResource("login.fxml"));
        AnchorPane userTaskLayout = userLoader.load();
        detailStage.setScene(new Scene(userTaskLayout));

        GUIController guiController = userLoader.getController();
        guiController.setController(controller,detailStage);
    }

    /**
     * Creates the chat Controller
     * @throws IOException file
     * @throws SQLException database
     */
    @FXML
    public void chat() throws IOException, SQLException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("chat-view.fxml"));

        AnchorPane root = (AnchorPane) loader.load();
        detailStage.setTitle("Chat");

        Scene scene = new Scene(root);
        detailStage.setScene(scene);

        ControllerChat controllerDetails = loader.getController();
        controllerDetails.setService(controller, detailStage, this.friend);
    }

    /**
     * Initialises all the tableViews used
     */
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

    /**
     * Visuals for the page
     * @param event
     */
    public void showFriends(ActionEvent event)
    {
        tableViewRequestsReceived.setVisible(false);
        tableViewRequestsSent.setVisible(false);
        deleteFriend.setVisible(true);
        acceptFriendRequest.setVisible(false);
        rejectFriendRequest.setVisible(false);
        searchFriend.setVisible(true);
        searchRequestReceived.setVisible(false);
        tableViewFriends.setVisible(true);
        cancelRequest.setVisible(false);
        endDate.setVisible(false);
        startDate.setVisible(false);
        reportActivities.setVisible(false);
    }

    /**
     * Visuals for the page
     * @param event
     */
    public void showSentRequests(ActionEvent event)
    {
        tableViewRequestsReceived.setVisible(false);
        tableViewRequestsSent.setVisible(true);
        deleteFriend.setVisible(false);
        acceptFriendRequest.setVisible(false);
        rejectFriendRequest.setVisible(false);
        searchFriend.setVisible(false);
        searchRequestReceived.setVisible(false);
        tableViewFriends.setVisible(false);
        cancelRequest.setVisible(true);
        endDate.setVisible(false);
        startDate.setVisible(false);
        reportActivities.setVisible(false);
    }

    /**
     * Visuals for the page
     * @param event
     */
    public void showReceivedRequests(ActionEvent event)
    {
        tableViewRequestsReceived.setVisible(true);
        tableViewRequestsSent.setVisible(false);
        deleteFriend.setVisible(false);
        acceptFriendRequest.setVisible(true);
        rejectFriendRequest.setVisible(true);
        searchFriend.setVisible(false);
        searchRequestReceived.setVisible(true);
        tableViewFriends.setVisible(false);
        cancelRequest.setVisible(false);
        endDate.setVisible(false);
        startDate.setVisible(false);
        reportActivities.setVisible(false);
    }

    public void showReport(ActionEvent event)
    {
        tableViewRequestsReceived.setVisible(false);
        tableViewRequestsSent.setVisible(false);
        deleteFriend.setVisible(false);
        acceptFriendRequest.setVisible(false);
        rejectFriendRequest.setVisible(false);
        searchFriend.setVisible(false);
        searchRequestReceived.setVisible(false);
        tableViewFriends.setVisible(false);
        cancelRequest.setVisible(false);
        endDate.setVisible(true);
        startDate.setVisible(true);
        reportActivities.setVisible(true);
    }

    /**
     * Function handler for cancelling a request
     * @param event
     * @throws SQLException database
     */
    public void cancelRequest(ActionEvent event) throws SQLException {
        FriendRequestDTO selectedUser = tableViewRequestsSent.getSelectionModel().getSelectedItem();
        if(selectedUser == null)
            MessageAlert.showErrorMessage(null, "No user selected!");
        else
            this.controller.deleteFriendRequest(this.friend.getId(),selectedUser.getId());
    }


    /**
     * Loads the data for the User
     * @param user The user selected
     * @throws SQLException
     */
    private void initModel(User user) throws SQLException {
        List<User> secondUsers = new ArrayList<User>();
        List<Friendship> friendships = controller.findFriendshipsForUser(user.getId());
        for(int i = 0; i < friendships.size(); i++){
            secondUsers.add(friendships.get(i).getSecondUser());
        }
        model.setAll(secondUsers);

    }

    /**
     * Updates the model (observer)
     * @param entityChangeEvent the event that occurred
     * @throws SQLException database
     */
    @Override
    public void update(EntityChangeEvent entityChangeEvent) throws SQLException {
        initModelRequestSent(friend);
    }

    /**
     * Load the data for the User
     * @param user The user selected
     * @throws SQLException
     */

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

    /**
     * Load the data for the user
     * @param user The user selected
     * @throws SQLException
     */
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

    /**
     * Filter to search friendRequests
     * @throws SQLException
     */
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

    /**
     * Filter for friendships
     * @throws SQLException
     */
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
