import com.company.Domain.Entity;
import com.company.Domain.User;
import com.company.Domain.Validators.UserValidator;
import com.company.Domain.Validators.ValidationException;
import com.company.Repository.File.UserFile;
import com.company.Repository.Memory.InMemoryRepositoryUser;
import com.company.Repository.RepoException;
import com.company.Repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

public class TestUserFileRepo {
    @Test
    public void TestAddUserFileRepo(){
        User user = new User("Ion", "Popica");
        user.setId(1L);
        try {
            UserRepository<Long, User> fileRepo = new UserFile<>("data/usersTest.csv", new UserValidator());
            fileRepo.save(user);
            Assertions.assertTrue(fileRepo.getSize() == 1);
        }
        catch (IOException | SQLException e){

        }
    }

    @Test
    public void TestDeleteUserFileRepo(){
        try {
            UserRepository<Long, User> fileRepo = new UserFile<>("data/usersTest.csv", new UserValidator());
            fileRepo.delete(1L);
            Assertions.assertTrue(fileRepo.getSize() == 0);
        }
        catch (IOException | SQLException e){

        }
    }

    @Test
    public void TestFindUserFileRepo(){
        try {
            UserRepository<Long, User> fileRepo = new UserFile<>("data/usersTest.csv", new UserValidator());
            Assertions.assertTrue(fileRepo.findOne(1L) == null);
        }
        catch (IOException | SQLException e){

        }
    }

    @Test
    public void TestUpdateUserFileRepo(){
        User user = new User("Mircea", "Pop");
        user.setId(1L);
        User user2 = new User("Ion", "Popica");
        user2.setId(1L);
        try {
            UserRepository<Long, User> fileRepo = new UserFile<>("data/usersTest.csv", new UserValidator());
            fileRepo.save(user2);
            fileRepo.update(user);
            Assertions.assertTrue(fileRepo.getSize() == 1);
            Assertions.assertTrue(fileRepo.findOne(1L).getFirstName().equals("Mircea"));
        }
        catch (IOException | SQLException e){

        }
    }

    @Test
    public void TestAll(){
        TestAddUserFileRepo();
        TestDeleteUserFileRepo();
        TestFindUserFileRepo();
        TestUpdateUserFileRepo();
    }
}
