package com.example.toysocialnetwork.Repository.Database;

import com.example.toysocialnetwork.Domain.Entity;
import com.example.toysocialnetwork.Domain.Friendship;
import com.example.toysocialnetwork.Domain.User;
import com.example.toysocialnetwork.Domain.Validators.Validator;
import com.example.toysocialnetwork.Repository.FriendshipRepository;
import com.example.toysocialnetwork.Repository.RepoException;
import com.example.toysocialnetwork.Repository.UserRepository;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFriendshipRepository<ID, E extends Entity<ID>, E1 extends Entity<ID>> implements FriendshipRepository<ID, E> {

    String url;
    String username;
    String password;
    private Validator<E> validator;
    private UserRepository<ID, E1> userRepository;

    public DatabaseFriendshipRepository(String url, String username, String password, Validator<E> validator, UserRepository<ID, E1> userRepository){
        this.url = url;
        this.password = password;
        this.username = username;
        this.validator = validator;
        this.userRepository = userRepository;
    }

    /**
     *
     * @param sql The SQL command
     * @return The SQL statement
     * @throws SQLException if the Statement can't be executed or is incorrect
     */
    PreparedStatement getStatement(String sql) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql);
        return ps;
    }

    /**
     *
     * @param friendship The friendship to be added in the database
     * @throws IOException Inheritance(file)
     * @throws SQLException if the Statement can't be executed or is incorrect
     */
    @Override
    public void addFriendshipRepo(E friendship) throws IOException, SQLException {
        validator.validate(friendship);

        if(findOneFriendship(friendship)==null) {
            String sql = "insert into friendships (id_user1, first_name_user1, last_name_user1, id_user2 " +
                    ",first_name_user2, last_name_user2, date ) values (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = getStatement(sql);
            Friendship friendship1 = (Friendship) friendship;
            friendship1.setDate(LocalDateTime.now());

            ps.setLong(1, friendship1.getFirstUser().getId());
            ps.setString(2, friendship1.getFirstUser().getFirstName());
            ps.setString(3, friendship1.getFirstUser().getLastName());
            ps.setLong(4, friendship1.getSecondUser().getId());
            ps.setString(5, friendship1.getSecondUser().getFirstName());
            ps.setString(6, friendship1.getSecondUser().getLastName());
            ps.setString(7, friendship1.getDate().toString());

            ps.executeUpdate();

            String sql2 = "insert into friendships (id_user1, first_name_user1, last_name_user1, id_user2 " +
                    ",first_name_user2, last_name_user2, date ) values (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps2 = getStatement(sql2);

            ps2.setLong(1, friendship1.getSecondUser().getId());
            ps2.setString(2, friendship1.getSecondUser().getFirstName());
            ps2.setString(3, friendship1.getSecondUser().getLastName());
            ps2.setLong(4, friendship1.getFirstUser().getId());
            ps2.setString(5, friendship1.getFirstUser().getFirstName());
            ps2.setString(6, friendship1.getFirstUser().getLastName());
            ps2.setString(7, friendship1.getDate().toString());

            ps2.executeUpdate();
        }
        else
            throw new RepoException("Friendship already exists");
    }

    /**
     *
     * @param friendship The friendship to be deleted
     * @throws IOException Inheritance(file)
     * @throws SQLException if the Statement can't be executed or is incorrect
     */
    @Override
    public void deleteFriendshipRepo(E friendship) throws IOException, SQLException {
        validator.validate(friendship);
        Friendship friendship1 = (Friendship) friendship;
        ID idUser1 = (ID) friendship1.getFirstUser().getId();
        ID idUser2 = (ID) friendship1.getSecondUser().getId();
        String sql = "delete from friendships where id_user1 = " + idUser1 + " and id_user2 = " + idUser2;

        PreparedStatement ps = getStatement(sql);

        ps.executeUpdate();

    }

    /**
     *
     * @return A list representing all the friendships in the database
     * @throws SQLException if the Statement can't be executed or is incorrect
     */
    @Override
    public List<E> findAllFriendships() throws SQLException {
        List<E> friendshipsList = new ArrayList<>();
        String sql = "select * from friendships";

        PreparedStatement ps = getStatement(sql);

        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            Long idUser1 = resultSet.getLong(1);
            User user1 = (User)userRepository.findOne((ID)idUser1);
            //User user1 = new User(resultSet.getString(2), resultSet.getString(3));
            user1.setId(resultSet.getLong(1));
            Long idUser2 = resultSet.getLong(4);
            User user2 = (User)userRepository.findOne((ID)idUser2);
            //User user2 = new User(resultSet.getString(5), resultSet.getString(6));
            user2.setId(resultSet.getLong(4));
            Friendship friendship = new Friendship(user1, user2);
            friendshipsList.add((E) friendship);
        }
        return friendshipsList;
    }

    /**
     *
     * @return A list representing all the friendships of a user in the database
     * @throws SQLException if the Statement can't be executed or is incorrect
     */
    public List<E> findAllFriendshipsForUser(ID idUser) throws SQLException {
        List<E> friendshipsList = new ArrayList<>();
        String sql = "select * from friendships where id_user1 = " + idUser;

        PreparedStatement ps = getStatement(sql);

        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            Long idUser1 = resultSet.getLong(1);
            User user1 = (User)userRepository.findOne((ID)idUser1);
            //User user1 = new User(resultSet.getString(2), resultSet.getString(3));
            user1.setId(resultSet.getLong(1));
            Long idUser2 = resultSet.getLong(4);
            User user2 = (User)userRepository.findOne((ID)idUser2);
            //User user2 = new User(resultSet.getString(5), resultSet.getString(6));
            user2.setId(resultSet.getLong(4));
            Friendship friendship = new Friendship(user1, user2);
            friendship.setDate(LocalDateTime.parse(resultSet.getString(7)));
            friendshipsList.add((E) friendship);
        }
        return friendshipsList;
    }

    /**
     *
     * @param friendship The friendship to be searched for
     * @return The friendship if it exists, null otherwise
     * @throws SQLException if the Statement can't be executed or is incorrect
     */
    @Override
    public E findOneFriendship(E friendship) throws SQLException {
        validator.validate(friendship);
        Friendship friendship1 = (Friendship) friendship;
        ID idUser1 = (ID) friendship1.getFirstUser().getId();
        ID idUSer2 = (ID) friendship1.getSecondUser().getId();

        String sql = "select * from friendships where id_user1 = " + idUser1 + " and id_user2 = " + idUSer2;

        PreparedStatement ps = getStatement(sql);

        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            return friendship;
        }
        return null;
    }

    /**
     *
     * @param oldFriendship The oldFriendship to be updated
     * @param newFriendship The newFriendship that will replace the oldFriendship
     * @throws SQLException if the Statement can't be executed or is incorrect
     * @throws IOException Inheritance(file)
     */
    @Override
    public void updateFriendship(E oldFriendship, E newFriendship) throws SQLException, IOException {
        validator.validate(oldFriendship);
        validator.validate(newFriendship);
        Friendship friendship1 = (Friendship) oldFriendship;
        ID idUser1 = (ID) friendship1.getFirstUser().getId();
        ID idUSer2 = (ID) friendship1.getSecondUser().getId();

        String sql = "delete from friendships where id_user1 = " + idUser1 + " and id_user2 = " + idUSer2;

        PreparedStatement psOne = getStatement(sql);

        psOne.executeUpdate();

        addFriendshipRepo(newFriendship);
    }

    /**
     *
     * @return The number of friendships found in the database
     * @throws SQLException if the Statement can't be executed or is incorrect
     */
    @Override
    public int getSize() throws SQLException{
        int size = 0;
        String sql = "select count(*) from friendships ";
        PreparedStatement ps = getStatement(sql);

        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            size++;
        }
        return size;
    }

}
