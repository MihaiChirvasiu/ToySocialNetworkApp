import com.company.Domain.Message;
import com.company.Domain.User;
import com.company.Domain.Validators.MessageValidator;
import com.company.Domain.Validators.UserValidator;
import com.company.Repository.Database.DatabaseMessageRepository;
import com.company.Repository.Database.DatabaseUserRepository;
import com.company.Repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class TestsDatabaseMessages {
    @Test
    public void TestFindMessage(){
        try {
            UserRepository<Long, User> databaseUserRepository = new DatabaseUserRepository<>("jdbc:postgresql://localhost:5432/testBase",
                    "postgres", "1234", new UserValidator());
            DatabaseMessageRepository<Long, Message, User> databaseMessageRepository = new DatabaseMessageRepository<>("jdbc:postgresql://localhost:5432/testBase",
                    "postgres", "1234", new MessageValidator(), databaseUserRepository);
            Message message = new Message(databaseUserRepository.findOne(1L), "Hello", LocalDateTime.now());
            databaseMessageRepository.addMessage(message, databaseUserRepository.findOne(2L));
            Assertions.assertTrue(databaseMessageRepository.findOneMessage(message.getDate()).getMessage().equals("Hello"));
        }
        catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
    }

    @Test
    public void TestConversation(){
        try {
            UserRepository<Long, User> databaseUserRepository = new DatabaseUserRepository<>("jdbc:postgresql://localhost:5432/testBase",
                    "postgres", "1234", new UserValidator());
            DatabaseMessageRepository<Long, Message, User> databaseMessageRepository = new DatabaseMessageRepository<>("jdbc:postgresql://localhost:5432/testBase",
                    "postgres", "1234", new MessageValidator(), databaseUserRepository);
            Assertions.assertTrue(databaseMessageRepository.getConversation(databaseUserRepository.findOne(1L), databaseUserRepository.findOne(2L)).size() == 12);
        }
        catch (SQLException throwables) {
            System.out.println("HELLLOO FROM EXCEPTION");
        }
    }
}
