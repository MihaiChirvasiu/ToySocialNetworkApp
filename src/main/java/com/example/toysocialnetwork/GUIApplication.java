package com.example.toysocialnetwork;

import com.example.toysocialnetwork.Domain.*;
import com.example.toysocialnetwork.Domain.Validators.FriendRequestValidator;
import com.example.toysocialnetwork.Domain.Validators.FriendshipValidator;
import com.example.toysocialnetwork.Domain.Validators.MessageValidator;
import com.example.toysocialnetwork.Domain.Validators.UserValidator;
import com.example.toysocialnetwork.Repository.Database.*;
import com.example.toysocialnetwork.Repository.FriendshipRepository;
import com.example.toysocialnetwork.Repository.UserRepository;
import com.example.toysocialnetwork.Service.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class GUIApplication extends Application {
    UserRepository<Long, User> repository = new DatabaseUserRepository<>("jdbc:postgresql://localhost:5432/lab7",
            "postgres", "1234", new UserValidator());
    FriendshipRepository<Long, Friendship> friendshipRepository = new DatabaseFriendshipRepository<>("jdbc:postgresql://localhost:5432/lab7",
            "postgres", "1234", new FriendshipValidator(), repository);
    DatabaseFriendRequestRepository<Long, FriendRequest, User> friendRequestRepository = new DatabaseFriendRequestRepository<>("jdbc:postgresql://localhost:5432/lab7",
            "postgres", "1234", new FriendRequestValidator(), repository);
    DatabaseGroupChatRepository<Long, GroupChat, User> groupChatRepository = new DatabaseGroupChatRepository<>("jdbc:postgresql://localhost:5432/lab7",
            "postgres", "1234", repository);
    DatabaseMessageRepository<Long, Message, User, GroupChat> messageRepository = new DatabaseMessageRepository<>("jdbc:postgresql://localhost:5432/lab7",
            "postgres", "1234", new MessageValidator(), repository, groupChatRepository);
    DatabaseEventRepository<Long, PublicEvent, User> eventRepository = new DatabaseEventRepository<>("jdbc:postgresql://localhost:5432/lab7",
            "postgres", "1234", repository);
    Controller<Long, User, Friendship, FriendRequest, Message, PublicEvent, GroupChat> controller = new Controller<>(repository, friendshipRepository, friendRequestRepository, messageRepository, eventRepository, groupChatRepository);

    /**
     * Start function for the GUI application
     * @param primaryStage the main stage
     * @throws Exception if it can't be shown
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        initView(primaryStage);
        primaryStage.setWidth(785);
        primaryStage.setHeight(543);
        primaryStage.show();

    }

    /**
     * Loads the stage
     * @param primaryStage the stage to be shown
     * @throws Exception
     */
    private void initView(Stage primaryStage) throws Exception{
        FXMLLoader userLoader = new FXMLLoader();
        userLoader.setLocation(getClass().getResource("login.fxml"));
        AnchorPane userTaskLayout = userLoader.load();
        primaryStage.setScene(new Scene(userTaskLayout));

        GUIController guiController = userLoader.getController();
        guiController.setController(controller,primaryStage);
    }

    public static void main(String[] args) {
        //System.out.println("ok");
        launch(args);
    }
}
