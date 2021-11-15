import com.company.Domain.User;
import com.company.Domain.Validators.UserValidator;
import com.company.Domain.Validators.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestsValidator {
    @Test
    public void TestValidate() {
        User user = new User("Abel", "Abelian");
        User user1 = new User("", "");
        user1.setId(1L);
        UserValidator userValidator = new UserValidator();
        try {
            userValidator.validate(user);
        }
        catch (ValidationException e){
            Assertions.assertTrue(e.getMessage().equals("ID cannot be null"));
        }
        try {
            userValidator.validate(user1);
        }
        catch (ValidationException e){
            Assertions.assertTrue(e.getMessage().equals("First name cannot be null"));
        }
        User user2 = new User("Ana", "Bl21321d");
        user2.setId(2L);
        try {
            userValidator.validate(user2);
        }
        catch (ValidationException e){
            Assertions.assertTrue(e.getMessage().equals("Last name is invalid"));
        }
        User user3 = new User("An!@2", "Bland");
        user3.setId(3L);
        try {
            userValidator.validate(user3);
        }
        catch (ValidationException e){
            Assertions.assertTrue(e.getMessage().equals("First name is invalid"));
        }
    }
}
