import com.company.Domain.Entity;
import com.company.Domain.Friendship;
import com.company.Domain.User;
import com.company.Domain.Validators.FriendshipValidator;
import com.company.Domain.Validators.UserValidator;
import com.company.Domain.Validators.ValidationException;
import com.company.Repository.Database.DatabaseFriendshipRepository;
import com.company.Repository.Database.DatabaseUserRepository;
import com.company.Repository.File.UserFile;
import com.company.Repository.FriendshipRepository;
import com.company.Repository.Memory.InMemoryRepositoryUser;
import com.company.Repository.RepoException;
import com.company.Repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

public class TestsFriendshipDatabaseRepository {
    @Test
    public void TestAddFriendshipDatabase(){
        try {
            FriendshipRepository<Long, Friendship> friendshipRepository = new DatabaseFriendshipRepository<>("jdbc:postgresql://localhost:5432/testBase",
                    "postgres", "1234", new FriendshipValidator());
            UserRepository<Long, User> userRepository = new DatabaseUserRepository<>("jdbc:postgresql://localhost:5432/testBase",
                    "postgres", "1234", new UserValidator());
            User user = new User("C", "D");
            user.setId(2L);
            User user2 = new User("C", "D");
            user2.setId(3L);
            userRepository.save(user);
            userRepository.save(user2);
            Friendship friendship = new Friendship(user, user2);
            friendshipRepository.addFriendshipRepo(friendship);
            Assertions.assertTrue(friendshipRepository.getSize() == 1);
        }
        catch (SQLException throwables) {

        }
        catch (IOException e) {

        }
    }

    @Test
    public void TestDeleteFriendshipDatabase(){
        try {
            FriendshipRepository<Long, Friendship> friendshipRepository = new DatabaseFriendshipRepository<>("jdbc:postgresql://localhost:5432/testBase",
                    "postgres", "1234", new FriendshipValidator());
            UserRepository<Long, User> userRepository = new DatabaseUserRepository<>("jdbc:postgresql://localhost:5432/testBase",
                    "postgres", "1234", new UserValidator());
            User user = new User("C", "D");
            user.setId(2L);
            User user2 = new User("C", "D");
            user2.setId(3L);
            userRepository.save(user);
            userRepository.save(user2);
            Friendship friendship = new Friendship(user, user2);
            friendshipRepository.addFriendshipRepo(friendship);
            Assertions.assertTrue(friendshipRepository.getSize() == 1);
            friendshipRepository.deleteFriendshipRepo(friendship);
            Assertions.assertTrue(friendshipRepository.getSize() == 0);
        }
        catch (SQLException throwables) {

        }
        catch (IOException e) {

        }
    }

    @Test
    public void TestUpdateFriendshipDatabase(){
        try {
            FriendshipRepository<Long, Friendship> friendshipRepository = new DatabaseFriendshipRepository<>("jdbc:postgresql://localhost:5432/testBase",
                    "postgres", "1234", new FriendshipValidator());
            UserRepository<Long, User> userRepository = new DatabaseUserRepository<>("jdbc:postgresql://localhost:5432/testBase",
                    "postgres", "1234", new UserValidator());
            User user = new User("C", "D");
            user.setId(2L);
            User user2 = new User("C", "D");
            user2.setId(3L);
            User user3 = new User("F", "E");
            user3.setId(4L);
            userRepository.save(user);
            userRepository.save(user2);
            Friendship friendship = new Friendship(user, user2);
            Friendship newFriendship = new Friendship(user, user3);
            friendshipRepository.addFriendshipRepo(friendship);
            friendshipRepository.updateFriendship(friendship, newFriendship);
            Assertions.assertTrue(friendshipRepository.getSize() == 1);
            Assertions.assertTrue(friendshipRepository.findOneFriendship(newFriendship).getSecondUser().getLastName().equals("E"));
        }
        catch (SQLException throwables) {

        }
        catch (IOException e) {

        }
    }
}
