package com.example.toysocialnetwork;

import com.example.toysocialnetwork.Domain.*;
import com.example.toysocialnetwork.Events.EntityChangeEvent;
import com.example.toysocialnetwork.Observer.Observer;
import com.example.toysocialnetwork.Service.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ControllerChat implements Observer<EntityChangeEvent> {

    @FXML
    private Button SendMessage;

    @FXML
    private Button Reply;

    @FXML
    private Button ReplyAll;

    @FXML
    private TableView<User> usersView;

    @FXML
    private TableColumn<User, String> userFirstName;

    @FXML
    private TableColumn<User, String> userLastName;

    @FXML
    private Label nameReceiver;

    @FXML
    private TextField searchReceiver;

    @FXML
    private Button backButton;

    Controller<Long, User, Friendship, FriendRequest, Message, PublicEvent> controller;
    ObservableList<Message> modelMessage = FXCollections.observableArrayList();
    ObservableList<User> model = FXCollections.observableArrayList();
    Stage detailStage;
    Scene mainScene;
    User friend;

    @Override
    public void update(EntityChangeEvent entityChangeEvent) throws SQLException, IOException {
        buildChatBox();
    }

    public void setService(Controller<Long, User, Friendship, FriendRequest, Message, PublicEvent> controller, Stage stage, User user) throws SQLException {
        this.controller = controller;
        controller.addObserver(this);
        this.detailStage = stage;
        this.friend = user;
        this.mainScene = detailStage.getScene();
        initModel();
    }

    @FXML
    public void initialize(){
        userFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("FirstName"));
        userLastName.setCellValueFactory(new PropertyValueFactory<User, String>("LastName"));
        searchReceiver.textProperty().addListener(o-> {
            try {
                handleFilter();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        usersView.setItems(model);
    }

    private void handleFilter() throws SQLException {
        Set<Long> keysSet = controller.getKeysServ();
        List<User> users = new ArrayList<User>();
        for(Long k : keysSet){
            users.add(controller.findOneServ(k));
        }
        Predicate<User> p1 = n -> n.getFirstName().toLowerCase().contains(searchReceiver.getText().toLowerCase());
        Predicate<User> p2 = n -> n.getLastName().toLowerCase().contains(searchReceiver.getText().toLowerCase());
        model.setAll(users.stream()
                .filter(p1.or(p2))
                .collect(Collectors.toList()));
    }


    private void initModel() throws SQLException {
        Set<Long> keysSet = controller.getKeysServ();
        List<User> users = new ArrayList<User>();
        for (Long k : keysSet) {
            users.add(controller.findOneServ(k));
        }
        model.setAll(users);
    }

    private void addMsg(Long senderID, String text) throws SQLException {
        List<Long> receiverID = new ArrayList<>();
        receiverID.add(senderID);
        controller.sendMessage(friend.getId(), receiverID, text, LocalDateTime.now());
    }

    private void replyMessage(Long receiverID, Message selectedMessage, String text) throws SQLException {
        controller.replyMessage(friend.getId(), receiverID, selectedMessage.getId(), text, LocalDateTime.now());
    }

    @FXML
    public void goBack() throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("friendrequests-view.fxml"));

        AnchorPane root = (AnchorPane) loader.load();
        detailStage.setTitle("FriendRequests");

        Scene scene = new Scene(root);
        detailStage.setScene(scene);

        ControllerDetails controllerDetails = loader.getController();
        controllerDetails.setService(controller, detailStage, this.friend);
    }


    public void buildChatBox() throws SQLException, IOException {
        User selectedUser = usersView.getSelectionModel().getSelectedItem();


        Button backToPublicChat = new Button("<");
        backToPublicChat.setId("backToPublicChat");
        backToPublicChat.setVisible(true);
        backToPublicChat.setOnAction(event -> {
            detailStage.setScene(mainScene);
            detailStage.show();
        });

        VBox vBox = new VBox();
        vBox.setId("chatHistory");
        vBox.setPrefWidth(770);
        vBox.setPrefHeight(200);

        vBox.setVisible(true);

        Text chatWindowInfo = new Text("public chat room");
        chatWindowInfo.setId("chatWindowInfo");

        TextField messageField = new TextField();
        messageField.setId("messageField");
        messageField.setPrefHeight(30);
        messageField.setPrefWidth(740);

        Button sendButton = new Button("send");
        sendButton.setId("sendMessage");
        sendButton.setVisible(true);
        sendButton.setOnAction(event -> {
            try {
                if(Objects.equals(messageField.getText(), ""))
                    MessageAlert.showErrorMessage(null, "No message written");
                else
                    addMsg(selectedUser.getId(), messageField.getText());
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e){
                MessageAlert.showErrorMessage(null, "No user selected");
            }
        });


        List<Message> messageList = controller.getConversationServ(friend.getId(), selectedUser.getId());
        for(int i = 0; i < messageList.size(); i++) {
            if (Objects.equals(messageList.get(i).getFromUser().getId(), friend.getId())) {

                Message selectedMessage = messageList.get(i);
                Label label = new Label(messageList.get(i).getMessage());
                label.setWrapText(true);
                Image playI=new Image("file:///E:/MAP/ReparareToySocialNetwork/Sources/replyIcon.jpeg");
                ImageView iv1=new ImageView(playI);
                iv1.setFitHeight(10);
                iv1.setFitWidth(10);
                Button reply = new Button("",iv1);
                reply.setOnAction(event -> {
                    try {
                        if(Objects.equals(messageField.getText(), ""))
                            MessageAlert.showErrorMessage(null, "No message written");
                        else
                            replyMessage(selectedUser.getId(), selectedMessage, messageField.getText());
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                    catch(NullPointerException e){
                        MessageAlert.showErrorMessage(null, "No user selected");
                    }
                });
                label.setId("receive");
                HBox hBox = new HBox();
                if(messageList.get(i).getReplyMessage() != null)
                    hBox.setBackground(new Background(new BackgroundFill(Color.AQUA, CornerRadii.EMPTY, Insets.EMPTY)));
                hBox.getChildren().add(label);
                hBox.getChildren().add(reply);
                hBox.setAlignment(Pos.BASELINE_RIGHT);
                vBox.getChildren().add(hBox);
                vBox.setSpacing(10);
                hBox.setVisible(true);
            }
            else {
                Message selectedMessage = messageList.get(i);
                Label label = new Label(messageList.get(i).getMessage());
                label.setWrapText(true);
                Image playI=new Image("file:///E:/MAP/ReparareToySocialNetwork/Sources/replyIcon.jpeg");
                ImageView iv1=new ImageView(playI);
                iv1.setFitHeight(10);
                iv1.setFitWidth(10);
                Button reply = new Button("",iv1);
                reply.setOnAction(event -> {
                    try {
                        if(Objects.equals(messageField.getText(), ""))
                            MessageAlert.showErrorMessage(null, "No message written");
                        else
                            replyMessage(selectedUser.getId(), selectedMessage, messageField.getText());
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                    catch(NullPointerException e){
                        MessageAlert.showErrorMessage(null, "No user selected");
                    }
                });
                label.setId("send");
                HBox hBox = new HBox();
                if(messageList.get(i).getReplyMessage() != null)
                    hBox.setBackground(new Background(new BackgroundFill(Color.AQUA, CornerRadii.EMPTY, Insets.EMPTY)));
                hBox.getChildren().add(label);
                hBox.getChildren().add(reply);
                hBox.setAlignment(Pos.BASELINE_LEFT);
                vBox.getChildren().add(hBox);
                vBox.setSpacing(10);
                hBox.setVisible(true);
            }
        }

        /*TableView<Message> messageTableView = new TableView<>();

        TableColumn<Message, String> messageStringTableColumn = new TableColumn<>();

        messageTableView.getColumns().add(messageStringTableColumn);
        messageStringTableColumn.setCellValueFactory(new PropertyValueFactory<Message, String>("Message"));

        modelMessage.setAll(messageList);

        messageTableView.setItems(modelMessage);


        TextField messageFieldReply = new TextField();
        messageField.setId("messageFieldReply");
        messageField.setPrefHeight(30);
        messageField.setPrefWidth(190);

        Button replyButton = new Button("reply");
        replyButton.setId("replyMessage");
        replyButton.setVisible(true);
        replyButton.setOnAction(event -> {
            try {
                Message selectedMessage = messageTableView.getSelectionModel().getSelectedItem();
                replyMessage(selectedUser.getId(), selectedMessage, messageFieldReply.getText());
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            catch(NullPointerException e){
                MessageAlert.showErrorMessage(null, "No message selected");
            }
        });
*/
        modelMessage.setAll(messageList);
        ScrollPane scrlPane = new ScrollPane(vBox);
        scrlPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrlPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrlPane.setId("scrolPane");
        scrlPane.setPrefHeight(400);
        scrlPane.setPrefWidth(785);
        VBox chatBox = new VBox(new HBox(backToPublicChat, chatWindowInfo), scrlPane, new HBox(messageField, sendButton));
        chatBox.setId("chatBox");
        Scene scene = new Scene(chatBox, 785, 400);
        detailStage.setScene(scene);
        detailStage.show();



    }

}


