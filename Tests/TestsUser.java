import com.company.Domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestsUser {

    @Test
    public void TestUser(){
        User user = new User("Abel", "Abelian");
        user.setId(1L);
        Assertions.assertTrue(user.getFirstName().equals("Abel"));
        Assertions.assertTrue(user.getLastName().equals("Abelian"));
        Assertions.assertTrue(user.getId().equals(1L));
        user.setFirstName("Ana");
        user.setLastName("Blandiana");
        Assertions.assertTrue(user.getFirstName().equals("Ana"));
        Assertions.assertTrue(user.getLastName().equals("Blandiana"));

    }
}
