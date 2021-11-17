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
        Assertions.assertTrue(friendRequest.getStatus().equals(STATUS.PENDING));
        Assertions.assertTrue(friendRequest.getIdUser1().getId().equals(1L));
        Assertions.assertTrue(friendRequest.getIdUser2().getId().equals(2L));
        friendRequest.acceptRequest();
        Assertions.assertTrue(friendRequest.getStatus().equals(STATUS.APPROVED));
        friendRequest.rejectRequest();
        Assertions.assertTrue(friendRequest.getStatus().equals(STATUS.REJECTED));
    }
}
