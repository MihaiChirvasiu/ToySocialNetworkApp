/*
import com.company.Domain.Friendship;
import com.company.Domain.User;
import com.company.Domain.Validators.FriendshipValidator;
import com.company.Domain.Validators.UserValidator;
import com.company.Repository.FriendshipRepository;
import com.company.Repository.Memory.InMemoryRepositoryFriendship;
import com.company.Repository.Memory.InMemoryRepositoryUser;
import com.company.Repository.UserRepository;
import com.company.Service.Controller;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

public class TestsController {
    @Test
    public void TestAddUserServ() {
        UserRepository<Long, User> repository = new InMemoryRepositoryUser<>(new UserValidator());
        FriendshipRepository<Long, Friendship> friendshipRepository = new InMemoryRepositoryFriendship<>(new FriendshipValidator());
        Controller<Long, User, Friendship> controller = new Controller<>(repository, friendshipRepository);
        User user = new User("Pop", "Popescu");
        user.setId(1L);
        try {
            controller.saveServ(user);
        }
        catch (IOException e){

        }
        catch (SQLException e){

        }
        try {
            Assertions.assertTrue(controller.getSizeServ() == 1);
        }
        catch (SQLException e){

        }
    }

    @Test
    public void TestDeleteUserServ(){
        UserRepository<Long, User> repository = new InMemoryRepositoryUser<>(new UserValidator());
        FriendshipRepository<Long, Friendship> friendshipRepository = new InMemoryRepositoryFriendship<>(new FriendshipValidator());
        Controller<Long, User, Friendship> controller = new Controller<>(repository, friendshipRepository);
        User user = new User("Pop", "Popescu");
        user.setId(1L);
        User user1 = new User("Aurel", "Vlaicu");
        user1.setId(2L);
        try {
            controller.saveServ(user);
            controller.saveServ(user1);
            controller.addFriendshipServ(1L, 2L);
            controller.deleteServ(1L);
        }
        catch (IOException e){

        }
        catch (SQLException e){

        }
        try {
            Assertions.assertTrue(controller.getSizeServ() == 1);
            Assertions.assertTrue(controller.findFriendships().size() == 0);
        }
        catch (SQLException e){

        }
    }

    @Test
    public void TestFindOneServ(){
        UserRepository<Long, User> repository = new InMemoryRepositoryUser<>(new UserValidator());
        FriendshipRepository<Long, Friendship> friendshipRepository = new InMemoryRepositoryFriendship<>(new FriendshipValidator());
        Controller<Long, User, Friendship> controller = new Controller<>(repository, friendshipRepository);
        User user = new User("Pop", "Popescu");
        user.setId(1L);
        try {
            controller.saveServ(user);
        }
        catch (IOException e){

        }
        catch (SQLException e){

        }
        try {
            Assertions.assertTrue(controller.findOneServ(1L).getFirstName().equals("Pop"));
        }
        catch (SQLException e){

        }
    }

    @Test
    public void TestUpdateServ(){
        UserRepository<Long, User> repository = new InMemoryRepositoryUser<>(new UserValidator());
        FriendshipRepository<Long, Friendship> friendshipRepository = new InMemoryRepositoryFriendship<>(new FriendshipValidator());
        Controller<Long, User, Friendship> controller = new Controller<>(repository, friendshipRepository);
        User user = new User("Pop", "Popescu");
        user.setId(1L);
        User user1 = new User("Mircea", "Vlaicu");
        user1.setId(1L);
        try {
            controller.saveServ(user);
            controller.updateServ(user1);
        }
        catch (IOException e){

        }
        catch (SQLException e){

        }
        try {
            Assertions.assertTrue(controller.findOneServ(1L).getFirstName().equals("Mircea"));
        }
        catch (SQLException e){

        }
    }

    @Test
    public void TestAddFriendship(){
        UserRepository<Long, User> repository = new InMemoryRepositoryUser<>(new UserValidator());
        FriendshipRepository<Long, Friendship> friendshipRepository = new InMemoryRepositoryFriendship<>(new FriendshipValidator());
        Controller<Long, User, Friendship> controller = new Controller<>(repository, friendshipRepository);
        User user = new User("Pop", "Popescu");
        user.setId(1L);
        User user1 = new User("Mircea", "Vlaicu");
        user1.setId(2L);
        try {
            controller.saveServ(user);
            controller.saveServ(user1);
            controller.addFriendshipServ(1L, 2L);
        }
        catch (IOException e){

        }
        catch (SQLException e){

        }
        try {
            Assertions.assertTrue(controller.findFriendships().size() == 1);
        }
        catch (SQLException e){

        }
    }

    @Test
    public void TestDeleteFriendship(){
        UserRepository<Long, User> repository = new InMemoryRepositoryUser<>(new UserValidator());
        FriendshipRepository<Long, Friendship> friendshipRepository = new InMemoryRepositoryFriendship<>(new FriendshipValidator());
        Controller<Long, User, Friendship> controller = new Controller<>(repository, friendshipRepository);
        User user = new User("Pop", "Popescu");
        user.setId(1L);
        User user1 = new User("Mircea", "Vlaicu");
        user1.setId(2L);
        try {
            controller.saveServ(user);
            controller.saveServ(user1);
            controller.addFriendshipServ(1L, 2L);
            controller.deleteFriendshipServ(1L, 2L);
        }
        catch (IOException e){

        }
        catch (SQLException e){

        }
        try {
            Assertions.assertTrue(controller.findFriendships().size() == 0);
        }
        catch (SQLException e){

        }
    }

    @Test
    public void TestFindFriendship() {
        UserRepository<Long, User> repository = new InMemoryRepositoryUser<>(new UserValidator());
        FriendshipRepository<Long, Friendship> friendshipRepository = new InMemoryRepositoryFriendship<>(new FriendshipValidator());
        Controller<Long, User, Friendship> controller = new Controller<>(repository, friendshipRepository);
        User user = new User("Pop", "Popescu");
        user.setId(1L);
        User user1 = new User("Mircea", "Vlaicu");
        user1.setId(2L);
        try {
            controller.saveServ(user);
            controller.saveServ(user1);
            controller.addFriendshipServ(1L, 2L);
        }
        catch (IOException e){

        }
        catch (SQLException e){

        }
        try {
            Assertions.assertTrue(controller.findFriendshipServ(1L, 2L) != null);
        }
        catch (SQLException e){

        }
    }

    @Test
    public void TestUpdateFriendship(){
        UserRepository<Long, User> repository = new InMemoryRepositoryUser<>(new UserValidator());
        FriendshipRepository<Long, Friendship> friendshipRepository = new InMemoryRepositoryFriendship<>(new FriendshipValidator());
        Controller<Long, User, Friendship> controller = new Controller<>(repository, friendshipRepository);
        User user = new User("Pop", "Popescu");
        user.setId(1L);
        User user1 = new User("Mircea", "Vlaicu");
        user1.setId(2L);
        User user2 = new User("Mircea", "Pop");
        user2.setId(3L);
        try {
            controller.saveServ(user);
            controller.saveServ(user1);
            controller.saveServ(user2);
            controller.addFriendshipServ(1L, 2L);
            controller.updateFriendshipServ(1L, 2L, 3L);
        }
        catch (IOException e){

        }
        catch (SQLException e){

        }
        try {
            Assertions.assertTrue(controller.findFriendships().get(0).getSecondUser().getLastName().equals("Pop"));
        }
        catch (SQLException e){

        }
    }
}
*/
