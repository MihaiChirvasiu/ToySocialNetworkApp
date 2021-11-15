import com.company.Domain.Friendship;
import com.company.Domain.User;
import com.company.Domain.Validators.FriendshipValidator;
import com.company.Repository.File.FriendshipFile;
import com.company.Repository.FriendshipRepository;
import com.company.Repository.Memory.InMemoryRepositoryFriendship;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

public class TestsFriendshipFileRepo {

    @Test
    public void AddFriendshipFile(){
        User user1 = new User("Ion", "Popica");
        user1.setId(1L);
        User user2 = new User("Abel", "Abelian");
        user2.setId(2L);
        try {
            FriendshipRepository<Long, Friendship> friendshipRepository = new FriendshipFile<>("data/friendshipsTest.csv", new FriendshipValidator());
            Friendship friendship = new Friendship(user1, user2);
            friendshipRepository.addFriendshipRepo(friendship);
            Assertions.assertTrue(friendshipRepository.getSize() == 1);
        }
        catch (IOException | SQLException e) {

        }
    }

    @Test
    public void DeleteFriendshipFile(){
        User user1 = new User("Ion", "Popica");
        user1.setId(1L);
        User user2 = new User("Abel", "Abelian");
        user2.setId(2L);
        try {
            FriendshipRepository<Long, Friendship> friendshipRepository = new FriendshipFile<>("data/friendshipsTest.csv", new FriendshipValidator());
            Friendship friendship = new Friendship(user1, user2);
            friendshipRepository.deleteFriendshipRepo(friendship);
            Assertions.assertTrue(friendshipRepository.getSize() == 0);
        }
        catch (IOException | SQLException e) {

        }
    }

    @Test
    public void FindOneFriendshipFile(){
        User user1 = new User("Ion", "Popica");
        user1.setId(1L);
        User user2 = new User("Abel", "Abelian");
        user2.setId(2L);
        try {
            FriendshipRepository<Long, Friendship> friendshipRepository = new FriendshipFile<>("data/friendshipsTest.csv", new FriendshipValidator());
            Friendship friendship = new Friendship(user1, user2);
            Assertions.assertTrue(friendshipRepository.findOneFriendship(friendship) == null);
        }
        catch (IOException | SQLException e) {

        }
    }

    @Test
    public void UpdateFriendshipFile(){
        User user1 = new User("Ion", "Popica");
        user1.setId(1L);
        User user2 = new User("Abel", "Abelian");
        user2.setId(2L);
        User user3 = new User("A", "B");
        user3.setId(3L);
        try {
            FriendshipRepository<Long, Friendship> friendshipRepository = new FriendshipFile<>("data/friendshipsTest.csv", new FriendshipValidator());
            Friendship friendship = new Friendship(user1, user2);
            Friendship newFriendship = new Friendship(user1, user3);
            friendshipRepository.addFriendshipRepo(friendship);
            Assertions.assertTrue(friendshipRepository.getSize() == 1);
            friendshipRepository.updateFriendship(friendship, newFriendship);
            Assertions.assertTrue(friendshipRepository.findOneFriendship(newFriendship) != null);
            friendshipRepository.deleteFriendshipRepo(newFriendship);
        }
        catch (IOException | SQLException e) {

        }
    }
}
