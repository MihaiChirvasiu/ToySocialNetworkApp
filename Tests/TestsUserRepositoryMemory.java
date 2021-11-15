import com.company.Domain.Entity;
import com.company.Domain.User;
import com.company.Domain.Validators.UserValidator;
import com.company.Domain.Validators.ValidationException;
import com.company.Repository.Memory.InMemoryRepositoryUser;
import com.company.Repository.RepoException;
import com.company.Repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

public class TestsUserRepositoryMemory {
    @Test
    public void TestUserRepoAdd(){
        UserRepository<Long, User>  UserRepository = new InMemoryRepositoryUser<>(new UserValidator());
        User user = new User("Abel", "Abelian");
        user.setId(1L);
        try {
            UserRepository.save(user);
        }
        catch (SQLException e){

        }
        catch (IOException e){

        }
        try {
            Assertions.assertTrue(UserRepository.getSize() == 1);
        }
        catch (SQLException e){

        }
        User user1 = new User("Ana", "Balna");
        user1.setId(1L);
        try{
            UserRepository.save(user);
        }
        catch (SQLException e){

        }
        catch (IOException e){

        }
        try{
            Assertions.assertTrue(UserRepository.findOne(1L).getFirstName().equals("Abel"));
        }
        catch (SQLException throwables) {

        }
    }

    @Test
    public void TestUserRepoFind(){
        UserRepository<Long, User>  UserRepository = new InMemoryRepositoryUser<>(new UserValidator());
        User user = new User("Ana", "Apopei");
        user.setId(1L);
        try {
            UserRepository.save(user);
        }
        catch (SQLException throwables) {

        }
        catch (IOException e) {

        }
        try {
            Assertions.assertTrue(UserRepository.findOne(1L).getFirstName().equals("Ana"));
        }
        catch (SQLException throwables) {

        }
        try {
            UserRepository.findOne(null);
        }
        catch (SQLException throwables) {

        }
        catch (RepoException e){
            Assertions.assertTrue(e.getErrorMessage().equals("Id must be not null"));
        }
    }

    @Test
    public void TestUserRepoDelete(){
        UserRepository<Long, User>  UserRepository = new InMemoryRepositoryUser<>(new UserValidator());
        User user = new User("Ana", "Apopei");
        user.setId(1L);
        try {
            UserRepository.delete(1L);
        }
        catch (SQLException throwables) {

        }
        catch (IOException e) {

        }
        try {
            Assertions.assertTrue(UserRepository.getSize() == 0);
        }
        catch (SQLException throwables) {

        }
        try {
            UserRepository.delete(2L);
        }
        catch (SQLException throwables) {

        }
        catch (IOException e) {

        }
    }

    @Test
    public void TestUserRepoUpdate(){
        UserRepository<Long, User>  UserRepository = new InMemoryRepositoryUser<>(new UserValidator());
        User user = new User("Ana", "Apopei");
        user.setId(1L);
        User newUser = new User("Abel", "Abelian");
        newUser.setId(1L);
        try {
            UserRepository.save(user);
        }
        catch (SQLException throwables) {

        }
        catch (IOException e) {

        }
        try {
            UserRepository.update(newUser);
        }
        catch (SQLException e){

        }
        catch (IOException e){

        }
        try {
            Assertions.assertTrue(UserRepository.getSize() == 1);
            Assertions.assertTrue(UserRepository.findOne(1L).getFirstName().equals("Abel"));
        }
        catch (SQLException throwables) {

        }
    }
}
