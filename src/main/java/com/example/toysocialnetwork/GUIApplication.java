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
    UserRepository<Long, User> repository = new DatabaseUserRepository<>("jdbc:postgresql://localhost:5432/Repository Lab5",
            "postgres", "Oana0910Andreea", new UserValidator());
    FriendshipRepository<Long, Friendship> friendshipRepository = new DatabaseFriendshipRepository<>("jdbc:postgresql://localhost:5432/Repository Lab5",
            "postgres", "Oana0910Andreea", new FriendshipValidator(), repository);
    DatabaseFriendRequestRepository<Long, FriendRequest, User> friendRequestRepository = new DatabaseFriendRequestRepository<>("jdbc:postgresql://localhost:5432/Repository Lab5",
            "postgres", "Oana0910Andreea", new FriendRequestValidator(), repository);
    DatabaseMessageRepository<Long, Message, User> messageRepository = new DatabaseMessageRepository<>("jdbc:postgresql://localhost:5432/Repository Lab5",
            "postgres", "Oana0910Andreea", new MessageValidator(), repository);
    DatabaseEventRepository<Long, PublicEvent, User> eventRepository = new DatabaseEventRepository<>("jdbc:postgresql://localhost:5432/Repository Lab5",
            "postgres", "Oana0910Andreea", repository);
    Controller<Long, User, Friendship, FriendRequest, Message, PublicEvent> controller = new Controller<>(repository, friendshipRepository, friendRequestRepository, messageRepository, eventRepository);

    @Override
    public void start(Stage primaryStage) throws Exception {

        initView(primaryStage);
        primaryStage.setWidth(600);
        primaryStage.show();

    }

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
