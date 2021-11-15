import com.company.Domain.Friendship;
import com.company.Domain.User;
import com.company.Domain.Validators.FriendshipValidator;
import com.company.Repository.FriendshipRepository;
import com.company.Repository.Memory.InMemoryRepositoryFriendship;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

public class TestsRepoFriendship {
    @Test
    public void TestAddFriendship(){
        FriendshipRepository<Long, Friendship> friendshipRepo = new InMemoryRepositoryFriendship<>(new FriendshipValidator());
        User user1 = new User("A", "B");
        user1.setId(1L);
        User user2 = new User("C", "D");
        user2.setId(2L);
        Friendship friendship = new Friendship(user1, user2);
        try {
            friendshipRepo.addFriendshipRepo(friendship);
            Assertions.assertTrue(friendshipRepo.getSize() == 1);
        }
        catch (IOException e){

        }
        catch (SQLException e){

        }
    }

    @Test
    public void TestDeleteFriendship(){
        FriendshipRepository<Long, Friendship> friendshipRepo = new InMemoryRepositoryFriendship<>(new FriendshipValidator());
        User user1 = new User("A", "B");
        user1.setId(1L);
        User user2 = new User("C", "D");
        user2.setId(2L);
        Friendship friendship = new Friendship(user1, user2);
        try {
            friendshipRepo.addFriendshipRepo(friendship);
        }
        catch (IOException | SQLException e) {

        }
        try {
            Assertions.assertTrue(friendshipRepo.getSize() == 1);
        }
        catch (SQLException e){

        }
        try {
            friendshipRepo.deleteFriendshipRepo(friendship);
        }
        catch (IOException | SQLException e) {

        }
        try {
            Assertions.assertTrue(friendshipRepo.getSize() == 0);
        }
        catch (SQLException e){

        }
    }

    @Test
    public void TestFindFriendship(){
        FriendshipRepository<Long, Friendship> friendshipRepo = new InMemoryRepositoryFriendship<>(new FriendshipValidator());
        User user1 = new User("A", "B");
        user1.setId(1L);
        User user2 = new User("C", "D");
        user2.setId(2L);
        Friendship friendship = new Friendship(user1, user2);
        try {
            friendshipRepo.addFriendshipRepo(friendship);
        }
        catch (IOException | SQLException e) {

        }
        try {
            Assertions.assertTrue(friendshipRepo.findOneFriendship(friendship).getFirstUser().equals(user1));
        }
        catch (SQLException e){

        }
    }

    @Test
    public void TestFindAll(){
        FriendshipRepository<Long, Friendship> friendshipRepo = new InMemoryRepositoryFriendship<>(new FriendshipValidator());
        User user1 = new User("A", "B");
        user1.setId(1L);
        User user2 = new User("C", "D");
        user2.setId(2L);
        Friendship friendship = new Friendship(user1, user2);
        try {
            friendshipRepo.addFriendshipRepo(friendship);
        }
        catch (IOException | SQLException e) {

        }
        try {
            Assertions.assertTrue(friendshipRepo.findAllFriendships().size() == 1);
        }
        catch (SQLException e){

        }
    }

    @Test
    public void TestUpdateFriendship(){
        FriendshipRepository<Long, Friendship> friendshipRepo = new InMemoryRepositoryFriendship<>(new FriendshipValidator());
        User user1 = new User("A", "B");
        user1.setId(1L);
        User user2 = new User("C", "D");
        user2.setId(2L);
        User user3 = new User("E", "F");
        user2.setId(3L);
        Friendship friendship = new Friendship(user1, user2);
        Friendship newFriendship = new Friendship(user1, user3);
        try {
            friendshipRepo.addFriendshipRepo(friendship);
        }
        catch (IOException | SQLException e) {

        }
        try {
            friendshipRepo.updateFriendship(friendship, newFriendship);
        }
        catch (IOException | SQLException e) {

        }
        try {
            Assertions.assertTrue(friendshipRepo.findAllFriendships().get(0).getSecondUser().equals(user3));
        }
        catch (SQLException e){

        }
    }
}
