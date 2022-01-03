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
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GUIController implements Observer<EntityChangeEvent> {

    Controller<Long, User, Friendship, FriendRequest, Message> controller;
    ObservableList<User> model = FXCollections.observableArrayList();
    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, String> tableColumnFirstName;

    @FXML
    private TableColumn<User, String> tableColumnLastName;

    public void setController(Controller<Long, User, Friendship, FriendRequest, Message> controller) throws SQLException {
        this.controller = controller;
        controller.addObserver(this);
        initModel();
    }

    @Override
    public void update(EntityChangeEvent entityChangeEvent) throws SQLException {
        initModel();
    }

    @FXML
    public void initialize(){
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("FirstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("LastName"));
        tableView.setItems(model);
    }

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
        model.setAll(users);

    }

    /**
     * Loads another window with details if a User is selected
     * @param ev
     * @throws IOException
     * @throws SQLException
     */
    @FXML
    public void handleFriendRequests(ActionEvent ev) throws IOException, SQLException {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if(selectedUser != null)
            showUserFriendRequest(selectedUser);
    }

    public void showUserFriendRequest(User user) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("friendrequests-view.fxml"));

        AnchorPane root = (AnchorPane) loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("FriendRequests");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        //dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);

        ControllerDetails controllerDetails = loader.getController();
        controllerDetails.setService(controller, dialogStage, user);

        dialogStage.show();

    }

}
