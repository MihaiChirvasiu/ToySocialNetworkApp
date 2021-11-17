import com.company.Domain.FriendRequest;
import com.company.Domain.User;
import com.company.Utils.STATUS;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestsFriendRequest {
    @Test
    public void TestGettersSetter(){
        User user1 = new User("Abel", "Abelian");
        user1.setId(1L);
        User user2 = new User("Ana", "Popica");
        user2.setId(2L);
        FriendRequest friendRequest = new FriendRequest(user1, user2);
        Assertions.assertTrue(friendRequest.getStatus().equals(STATUS.pending));
        Assertions.assertTrue(friendRequest.getUser1().equals(user1));
        Assertions.assertTrue(friendRequest.getUser2().equals(user2));
        friendRequest.acceptRequest();
        Assertions.assertTrue(friendRequest.getStatus().equals(STATUS.approved));
        friendRequest.rejectRequest();
        Assertions.assertTrue(friendRequest.getStatus().equals(STATUS.rejected));
    }
}
