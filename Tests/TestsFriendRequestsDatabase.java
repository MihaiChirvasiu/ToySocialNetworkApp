import com.company.Domain.FriendRequest;
import com.company.Domain.User;
import com.company.Domain.Validators.FriendRequestValidator;
import com.company.Repository.Database.DatabaseFriendRequestRepository;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class TestsFriendRequestsDatabase {
    @Test
    public void AddFriendRequestDatabase(){
        try {
            DatabaseFriendRequestRepository<Long, FriendRequest> friendRequestRepository =
                    new DatabaseFriendRequestRepository<>("jdbc:postgresql://localhost:5432/testBase", "postgres",
                            "1234", new FriendRequestValidator());
            User user1 = new User("Anabella", "Kaufland");
            user1.setId(1L);
            User user2 = new User("Anabella", "Kaufland");
            user2.setId(2L);
            FriendRequest friendRequest = new FriendRequest(user1, user2);
            friendRequestRepository.addFriendRequest(friendRequest);
        }
        catch (SQLException throwables) {

        }
    }
}
