package com.example.toysocialnetwork.Repository.Database;

import com.example.toysocialnetwork.Domain.Entity;
import com.example.toysocialnetwork.Domain.User;
import com.example.toysocialnetwork.Domain.Validators.Validator;
import com.example.toysocialnetwork.Repository.Memory.InMemoryRepositoryUser;
import com.example.toysocialnetwork.Repository.UserRepository;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class DatabaseUserRepository<ID, E extends Entity<ID>> implements UserRepository<ID, E>  {

    private String url;
    private String username;
    private String password;
    private Validator<E> validator;

    public DatabaseUserRepository(String url, String username, String password, Validator<E> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    /**
     *
     * @param sql The SQL command
     * @return The SQL statement
     * @throws SQLException if the Statement can't be executed or is incorrect
     */
    private PreparedStatement getStatement(String sql) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql);
        return ps;
    }

    private void generateID(User user) throws SQLException{
        while(true){
            long randomID = ThreadLocalRandom.current().nextInt(0, 10001);
            String sql = "select id from users where id = " + randomID;
            PreparedStatement ps = getStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            if(!resultSet.next()) {
                user.setId(randomID);
                return;
            }
        }
    }

    @Override
    public String findEmail(String email) throws SQLException{
        String sql = "select email from users where email = '" + email + "'";

        PreparedStatement ps = getStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        if(resultSet.next())
            return resultSet.getString("email");
        else
            return null;
    }

    @Override
    public User loginRepo(String email, String password) throws SQLException{
        String sql = "select * from users where email = '" + email + "' and password = '" + password + "'";

        PreparedStatement ps = getStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        if(resultSet.next()) {
                User user1 = new User(resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
                user1.setId(resultSet.getLong(1));
                return user1;
            }
        else
            return null;
    }

    /**
     *
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return The user that has the given ID
     * @throws SQLException if the Statement can't be executed or is incorrect
     */
    @Override
    public E findOne(ID id) throws SQLException {
        String sql = "select * from users where id = " + id;

        PreparedStatement ps = getStatement(sql);

        ResultSet resultSet = ps.executeQuery();
        resultSet.next();
        User user = new User(resultSet.getString("first_name"), resultSet.getString("last_name"),
                resultSet.getString(4), resultSet.getString(5));
        user.setId(resultSet.getLong("id"));
        return (E) user;
    }

    /**
     *
     * @return All the users in the database
     * @throws SQLException if the Statement can't be executed or is incorrect
     */
    @Override
    public Iterable<E> findAll() throws SQLException {
        Set<E> users = new HashSet<>();
        String sql = "SELECT * from users";
        PreparedStatement ps = getStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) {

            Long id = resultSet.getLong( "id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String email = resultSet.getString(4);
            String password = resultSet.getString(5);

            User user = new User(firstName, lastName, email, password);
            user.setId(id);
            users.add((E) user);
        }
        return users;
    }

    /**
     *
     * @param entity entity must be not null
     * @return null if the user was added in the database
     * @throws IOException Inheritance(file)
     * @throws SQLException if the Statement can't be executed or is incorrect
     */
    @Override
    public E save(E entity) throws IOException, SQLException{
        String sql = "insert into users (id, first_name, last_name, email, password ) values (?, ?, ?, ?, ?)";

        PreparedStatement ps = getStatement(sql);

        User user = (User) entity;
        generateID(user);
        validator.validate(entity);
        ps.setLong(1, user.getId());
        ps.setString(2, user.getFirstName());
        ps.setString(3, user.getLastName());
        ps.setString(4, user.getEmail());
        ps.setString(5, user.getPassword());

        ps.executeUpdate();

        return null;
    }

    /**
     *
     * @param id id must be not null
     * @return null if the operation was successful
     * @throws IOException Inheritance(file)
     * @throws SQLException if the Statement can't be executed or is incorrect
     */
    @Override
    public E delete(ID id) throws IOException, SQLException {
        String sql = "delete from users where id = " + id;
        PreparedStatement ps = getStatement(sql);

        ps.executeUpdate();

        return null;
    }

    /**
     *
     * @param entity entity must not be null
     * @return null if the operation was successful
     * @throws IOException Inheritance(file)
     * @throws SQLException if the Statement can't be executed or is incorrect
     */
    @Override
    public E update(E entity) throws IOException,SQLException {
        validator.validate(entity);
        String sql = "update users set first_name = ?, last_name = ? where id = ?";
        PreparedStatement ps = getStatement(sql);

        User user = (User) entity;
        ps.setLong(3, user.getId());
        ps.setString(1, user.getFirstName());
        ps.setString(2, user.getLastName());

        ps.executeUpdate();

        return null;
    }

    /**
     *
     * @return The number of all users found in the database
     * @throws SQLException if the Statement can't be executed or is incorrect
     */
    @Override
    public int getSize() throws SQLException{
        int size = 0;
        String sql = "select count(*) from users ";
        PreparedStatement ps = getStatement(sql);

        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            size++;
        }
        return size;
    }

    /**
     *
     * @return A set of IDs corresponding to the users found in the database
     * @throws SQLException if the Statement can't be executed or is incorrect
     */
    @Override
    public Set<ID> getKeys() throws SQLException {
        Set<ID> set = new HashSet<>();
        String sql = "select id from users ";
        PreparedStatement ps = getStatement(sql);

        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()) {
            Long id = resultSet.getLong("id");
            ID ID = (ID) id;
            set.add(ID);
        }
        return set;
    }

}
