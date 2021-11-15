package com.company;

import com.company.Domain.Friendship;
import com.company.Domain.User;
import com.company.Domain.Validators.FriendshipValidator;
import com.company.Domain.Validators.UserValidator;
import com.company.Repository.Database.DatabaseFriendshipRepository;
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
            UserRepository<Long, User> repository = new DatabaseUserRepository<>("jdbc:postgresql://localhost:5432/lab7", "postgres",
                    "1234", new UserValidator());
            FriendshipRepository<Long, Friendship> friendshipRepository = new DatabaseFriendshipRepository<>("jdbc:postgresql://localhost:5432/lab7",
                    "postgres", "1234", new FriendshipValidator());
            //UserRepository<Long, User> repository = new UserFile<>("data/users.csv", new UserValidator());
            //FriendshipRepository<Long, Friendship> friendshipRepository = new FriendshipFile<>("data/friendships.csv", new FriendshipValidator());
            //UserValidator validator = new UserValidator();
            //Repository<Long, User> repository = new InMemoryRepository<>(validator);
            Controller<Long, User, Friendship> controller = new Controller<>(repository, friendshipRepository);
            UI<Long, User, Friendship> ui = new UI<>(controller);
            ui.run();
        }
        catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Incorrect line");
        }
    }
}
