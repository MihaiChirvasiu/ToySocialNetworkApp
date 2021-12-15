package com.example.toysocialnetwork.Repository.Database;

import com.example.toysocialnetwork.Domain.Entity;
import com.example.toysocialnetwork.Domain.FriendRequest;
import com.example.toysocialnetwork.Domain.Friendship;
import com.example.toysocialnetwork.Domain.User;
import com.example.toysocialnetwork.Domain.Validators.Validator;
import com.example.toysocialnetwork.Repository.RepoException;
import com.example.toysocialnetwork.Repository.UserRepository;
import com.example.toysocialnetwork.Utils.STATUS;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFriendRequestRepository<ID, E extends Entity<ID>, E1 extends Entity<ID>> {

    String url;
    String username;
    String password;
    private Validator<E> validator;
    private UserRepository<ID,E1> userRepository;

    public DatabaseFriendRequestRepository(String url, String username, String password, Validator<E> validator, UserRepository<ID, E1> userRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.userRepository=userRepository;
    }

    /**
     *
     * @param sql The SQL command to be executed
     * @return The statement
     * @throws SQLException if the command is not a valid one
     */
    PreparedStatement getStatement(String sql) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql);
        return ps;
    }

    /**
     *
     * @param friendRequest The friendRequest to be searched for
     * @return The friendRequest if it exists, null otherwise
     * @throws SQLException if the command is not a valid one
     */
    public FriendRequest findFriendRequest(E friendRequest) throws SQLException {
        validator.validate(friendRequest);
        FriendRequest friendRequest1 = (FriendRequest) friendRequest;
        String sql = "select * from friendrequests where (id_user1 = " + friendRequest1.getUser1().getId() +
                " and id_user2 = " + friendRequest1.getUser2().getId() + " ) or (id_user1 = " + friendRequest1.getUser2().getId() +
                " and id_user2 = " + friendRequest1.getUser1().getId() + " )";
        PreparedStatement ps = getStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        if(resultSet.next()) {
            if(resultSet.getString(3).equals("approved"))
                friendRequest1.acceptRequest();
            else if(resultSet.getString(3).equals("rejected"))
                friendRequest1.rejectRequest();
            return friendRequest1;
        }
        return null;
    }

    public void deleteFriendRequest(E friendRequest) throws SQLException {
        validator.validate(friendRequest);
        FriendRequest friendRequest1 = (FriendRequest) friendRequest;
        if(findFriendRequest(friendRequest)!=null && findFriendRequest(friendRequest).getStatus().equals(STATUS.pending)) {
            String sql = "delete from friendrequests where id_user1 = " + friendRequest1.getUser1().getId() +
                    " and id_user2 = " + friendRequest1.getUser2().getId();
            PreparedStatement ps = getStatement(sql);
            ps.executeUpdate();
        }
        else
            throw new RepoException("Friend request does not exist");
    }

    /**
     *
     * @param friendRequest The friendRequest to be added in the database
     * @throws SQLException if the command is not a valid one
     */
    public void addFriendRequest(E friendRequest) throws SQLException {
        validator.validate(friendRequest);
        if(findFriendRequest(friendRequest)==null || !findFriendRequest(friendRequest).getStatus().equals(STATUS.pending)) {
            String sql = "insert into friendrequests (id_user1, id_user2, status, date ) values (?, ?, ?, ?)";

            PreparedStatement ps = getStatement(sql);
            FriendRequest friendRequest1 = (FriendRequest) friendRequest;
            friendRequest1.setStatus();

            ps.setLong(1, friendRequest1.getUser1().getId());
            ps.setLong(2, friendRequest1.getUser2().getId());
            ps.setString(3, friendRequest1.getStatus().toString());
            ps.setString(4, friendRequest1.getDate().toString());

            ps.executeUpdate();
        }
        else
            throw new RepoException("Friend request already exists");
    }

    /**
     *
     * @param friendRequest The friendRequest to be updated with a new status
     * @param newStatus The new status for the friendRequest
     * @throws SQLException if the command is not a valid one
     */
    public void updateStatus(E friendRequest, STATUS newStatus) throws SQLException{
        validator.validate(friendRequest);
        FriendRequest friendRequest1 = (FriendRequest) friendRequest;
        if(newStatus.equals(STATUS.rejected))
            friendRequest1.rejectRequest();
        else
            friendRequest1.acceptRequest();
        ID idUser1 = (ID) friendRequest1.getUser1().getId();
        ID idUser2 = (ID) friendRequest1.getUser2().getId();
        String sql = "update friendrequests set status = ? where id_user1 = " + idUser1 + " and id_user2 = " + idUser2 ;

        PreparedStatement ps = getStatement(sql);

        ps.setString(1, friendRequest1.getStatus().toString());

        ps.executeUpdate();
    }

    /**
     *
     * @param user The user that we will search all the friendRequests
     * @return A list of all friendRequests
     * @throws SQLException if the command is not a valid one
     */
    public List<E> findAllFriendRequestsForUser(User user) throws SQLException {
        List<E> friendRequestList = new ArrayList<>();
        String sql = "select * from friendrequests where id_user1 = " + user.getId() + " and status = 'pending' " ;

        PreparedStatement ps = getStatement(sql);

        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            Long idUser=resultSet.getLong(2);
            String user2FirstName=((User)userRepository.findOne((ID)idUser)).getFirstName();
            String user2LastName=((User)userRepository.findOne((ID)idUser)).getLastName();
            User user2 = new User(user2FirstName, user2LastName);
            user2.setId(resultSet.getLong(2));
            FriendRequest friendRequest = new FriendRequest(user, user2);
            friendRequest.setDate(LocalDateTime.parse(resultSet.getString(4)));
            friendRequestList.add((E) friendRequest);
        }
        return friendRequestList;
    }

    public List<E> findAllFriendRequestsForUserReceived(User user) throws SQLException {
        List<E> friendRequestList = new ArrayList<>();
        String sql = "select * from friendrequests where id_user2 = " + user.getId() + " and status = 'pending' " ;

        PreparedStatement ps = getStatement(sql);

        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            Long idUser=resultSet.getLong(1);
            String user2FirstName=((User)userRepository.findOne((ID)idUser)).getFirstName();
            String user2LastName=((User)userRepository.findOne((ID)idUser)).getLastName();
            User user2 = new User(user2FirstName, user2LastName);
            user2.setId(resultSet.getLong(1));
            FriendRequest friendRequest = new FriendRequest(user, user2);
            friendRequest.setDate(LocalDateTime.parse(resultSet.getString(4)));
            friendRequestList.add((E) friendRequest);
        }
        return friendRequestList;
    }

}
