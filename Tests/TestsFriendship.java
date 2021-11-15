import com.company.Domain.Friendship;
import com.company.Domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestsFriendship {
    @Test
    public void TestGettersSetter(){
        User user1 = new User("Ana", "Balna");
        user1.setId(1L);
        User user2 = new User("Abel", "Abelian");
        user2.setId(2L);
        User user3 = new User("C", "D");
        user3.setId(3L);
        Friendship friendship = new Friendship(user1, user2);
        Assertions.assertTrue(friendship.getFirstUser().equals(user1));
        Assertions.assertTrue(friendship.getSecondUser().equals(user2));
        friendship.setSecondUser(user3);
        Assertions.assertTrue(friendship.getSecondUser().equals(user3));
        friendship.setFirstUser(user2);
        Assertions.assertTrue(friendship.getFirstUser().equals(user2));
    }
}
