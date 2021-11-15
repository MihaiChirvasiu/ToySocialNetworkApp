import com.company.Domain.Friendship;
import com.company.Domain.User;
import com.company.Domain.Validators.UserValidator;
import com.company.Domain.Validators.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestsFriendshipValidator {

    @Test
    public void TestValidator(){
        User user1 = new User("A", "B");
        user1.setId(1L);
        try {
            Friendship friendship = new Friendship(user1, user1);
        }
        catch (ValidationException e){
            Assertions.assertTrue(e.getMessage().equals("Can't assign the same user as friend"));
        }
    }
}
