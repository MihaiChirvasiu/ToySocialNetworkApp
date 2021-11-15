import com.company.Domain.Entity;
import com.company.Domain.User;
import com.company.Domain.Validators.UserValidator;
import com.company.Domain.Validators.ValidationException;
import com.company.Repository.Database.DatabaseUserRepository;
import com.company.Repository.File.UserFile;
import com.company.Repository.Memory.InMemoryRepositoryUser;
import com.company.Repository.RepoException;
import com.company.Repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

public class TestsUserDatabaseRepository {
    @Test
    public void TestAddUserDatabase(){
        try{
            UserRepository<Long, User> userRepository = new DatabaseUserRepository<>("jdbc:postgresql://localhost:5432/testBase",
                    "postgres", "1234", new UserValidator());
            User user = new User("A", "B");
            user.setId(1L);
            userRepository.save(user);
            Assertions.assertTrue(userRepository.getSize() == 1);
        }
        catch (SQLException throwables) {

        }
        catch (IOException e) {

        }
    }

    @Test
    public void TestDeleteUserDatabase(){
        try {
            UserRepository<Long, User> userRepository = new DatabaseUserRepository<>("jdbc:postgresql://localhost:5432/testBase",
                    "postgres", "1234", new UserValidator());
            User user = new User("A", "B");
            user.setId(1L);
            userRepository.save(user);
            Assertions.assertTrue(userRepository.getSize() == 1);
            userRepository.delete(1L);
            Assertions.assertTrue(userRepository.getSize() == 0);
        }
        catch (SQLException throwables) {

        }
        catch (IOException e) {

        }
    }

    @Test
    public void TestUpdateUserDatabase(){
        try {
            UserRepository<Long, User> userRepository = new DatabaseUserRepository<>("jdbc:postgresql://localhost:5432/testBase",
                    "postgres", "1234", new UserValidator());
            User user = new User("A", "B");
            user.setId(1L);
            User user1 = new User("C", "D");
            user1.setId(1L);
            Assertions.assertTrue(userRepository.getSize() == 1);
            userRepository.update(user1);
            Assertions.assertTrue(userRepository.findOne(1L).getLastName().equals("D"));
        }
        catch (SQLException throwables) {

        }
        catch (IOException e) {

        }
    }
}
