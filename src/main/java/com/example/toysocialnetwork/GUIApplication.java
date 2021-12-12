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
    UserRepository<Long, User> repository = new DatabaseUserRepository<>("jdbc:postgresql://localhost:5432/lab7",
            "postgres", "1234", new UserValidator());
    FriendshipRepository<Long, Friendship> friendshipRepository = new DatabaseFriendshipRepository<>("jdbc:postgresql://localhost:5432/lab7",
            "postgres", "1234", new FriendshipValidator());
    DatabaseFriendRequestRepository<Long, FriendRequest> friendRequestRepository = new DatabaseFriendRequestRepository<>(
            "jdbc:postgresql://localhost:5432/lab7",
            "postgres", "1234", new FriendRequestValidator());
    DatabaseMessageRepository<Long, Message, User> messageRepository = new DatabaseMessageRepository<>("jdbc:postgresql://localhost:5432/lab7",
            "postgres", "1234", new MessageValidator(), repository);
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
        guiController.setController(controller);
    }

    public static void main(String[] args) {
        //System.out.println("ok");
        launch(args);
    }
}
