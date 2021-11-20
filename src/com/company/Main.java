package com.company;

import com.company.Domain.FriendRequest;
import com.company.Domain.Friendship;
import com.company.Domain.Message;
import com.company.Domain.User;
import com.company.Domain.Validators.FriendRequestValidator;
import com.company.Domain.Validators.FriendshipValidator;
import com.company.Domain.Validators.MessageValidator;
import com.company.Domain.Validators.UserValidator;
import com.company.Repository.Database.DatabaseFriendRequestRepository;
import com.company.Repository.Database.DatabaseFriendshipRepository;
import com.company.Repository.Database.DatabaseMessageRepository;
import com.company.Repository.Database.DatabaseUserRepository;
import com.company.Repository.File.FriendshipFile;
import com.company.Repository.File.UserFile;
import com.company.Repository.FriendshipRepository;
import com.company.Repository.UserRepository;
import com.company.Service.Controller;
import com.company.UI.UI;
import com.company.Utils.Constants;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {
	    try {
            UserRepository<Long, User> repository = new DatabaseUserRepository<>("jdbc:postgresql://localhost:5432/Repository Lab5",
                    "postgres", "Oana0910Andreea", new UserValidator());
            FriendshipRepository<Long, Friendship> friendshipRepository = new DatabaseFriendshipRepository<>("jdbc:postgresql://localhost:5432/Repository Lab5",
                    "postgres", "Oana0910Andreea", new FriendshipValidator());
            DatabaseFriendRequestRepository<Long, FriendRequest> friendRequestRepository = new DatabaseFriendRequestRepository<>(
                    "jdbc:postgresql://localhost:5432/Repository Lab5",
                    "postgres", "Oana0910Andreea", new FriendRequestValidator());
            DatabaseMessageRepository<Long, Message, User> messageRepository = new DatabaseMessageRepository<>("jdbc:postgresql://localhost:5432/Repository Lab5",
                    "postgres", "Oana0910Andreea", new MessageValidator(), repository);
            //UserRepository<Long, User> repository = new UserFile<>("data/users.csv", new UserValidator());
            //FriendshipRepository<Long, Friendship> friendshipRepository = new FriendshipFile<>("data/friendships.csv", new FriendshipValidator());
            //UserValidator validator = new UserValidator();
            //Repository<Long, User> repository = new InMemoryRepository<>(validator);
            Controller<Long, User, Friendship, FriendRequest, Message> controller = new Controller<>(repository, friendshipRepository, friendRequestRepository, messageRepository);
            UI<Long, User, Friendship, FriendRequest, Message> ui = new UI<>(controller);
            ui.run();
        }
        catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Incorrect line");
        }
    }
}
