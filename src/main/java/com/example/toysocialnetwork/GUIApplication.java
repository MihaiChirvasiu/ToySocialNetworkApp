package com.example.toysocialnetwork;

import com.example.toysocialnetwork.Domain.FriendRequest;
import com.example.toysocialnetwork.Domain.Friendship;
import com.example.toysocialnetwork.Domain.Message;
import com.example.toysocialnetwork.Domain.User;
import com.example.toysocialnetwork.Domain.Validators.FriendRequestValidator;
import com.example.toysocialnetwork.Domain.Validators.FriendshipValidator;
import com.example.toysocialnetwork.Domain.Validators.MessageValidator;
import com.example.toysocialnetwork.Domain.Validators.UserValidator;
import com.example.toysocialnetwork.Repository.Database.DatabaseFriendRequestRepository;
import com.example.toysocialnetwork.Repository.Database.DatabaseFriendshipRepository;
import com.example.toysocialnetwork.Repository.Database.DatabaseMessageRepository;
import com.example.toysocialnetwork.Repository.Database.DatabaseUserRepository;
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
            "postgres", "Oana0910Andreea", new FriendshipValidator());
    DatabaseFriendRequestRepository<Long, FriendRequest, User> friendRequestRepository = new DatabaseFriendRequestRepository<>("jdbc:postgresql://localhost:5432/Repository Lab5",
            "postgres", "Oana0910Andreea", new FriendRequestValidator(), repository);
    DatabaseMessageRepository<Long, Message, User> messageRepository = new DatabaseMessageRepository<>("jdbc:postgresql://localhost:5432/Repository Lab5",
            "postgres", "Oana0910Andreea", new MessageValidator(), repository);
    Controller<Long, User, Friendship, FriendRequest, Message> controller = new Controller<>(repository, friendshipRepository, friendRequestRepository, messageRepository);

    @Override
    public void start(Stage primaryStage) throws Exception {

        initView(primaryStage);
        primaryStage.setWidth(800);
        primaryStage.show();

    }

    private void initView(Stage primaryStage) throws Exception{
        FXMLLoader userLoader = new FXMLLoader();
        userLoader.setLocation(getClass().getResource("users-view.fxml"));
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
