package com.company.Repository.Database;

import com.company.Domain.Entity;
import com.company.Domain.FriendRequest;
import com.company.Domain.Friendship;
import com.company.Domain.User;
import com.company.Domain.Validators.Validator;
import com.company.Repository.RepoException;
import com.company.Utils.STATUS;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFriendRequestRepository<ID, E extends Entity<ID>> {

    String url;
    String username;
    String password;
    private Validator<E> validator;

    public DatabaseFriendRequestRepository(String url, String username, String password, Validator<E> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    PreparedStatement getStatement(String sql) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql);
        return ps;
    }

    public FriendRequest findFriendRequest(E friendRequest) throws SQLException
    {
        validator.validate(friendRequest);
        FriendRequest friendRequest1 = (FriendRequest) friendRequest;
        String sql = "select * from friendrequests where id_user1 = " + friendRequest1.getUser1().getId() +
                " and id_user2 = " + friendRequest1.getUser2().getId();
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

    public void addFriendRequest(E friendRequest) throws SQLException
    {
        validator.validate(friendRequest);
        if(findFriendRequest(friendRequest)==null || !findFriendRequest(friendRequest).getStatus().equals(STATUS.pending)) {
            String sql = "insert into friendrequests (id_user1, id_user2, status ) values (?, ?, ?)";

            PreparedStatement ps = getStatement(sql);
            FriendRequest friendRequest1 = (FriendRequest) friendRequest;
            friendRequest1.setStatus();

            ps.setLong(1, friendRequest1.getUser1().getId());
            ps.setLong(2, friendRequest1.getUser2().getId());
            ps.setString(3, friendRequest1.getStatus().toString());

            ps.executeUpdate();
        }
        else
            throw new RepoException("Friend request already exists");
    }

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

    public List<E> findAllFriendRequestsForUser(User user) throws SQLException {
        List<E> friendRequestList = new ArrayList<>();
        String sql = "select * from friendrequests where id_user1 = " + user.getId() + " and status = 'pending' " ;

        PreparedStatement ps = getStatement(sql);

        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            User user2 = new User("aa", "bb");
            user2.setId(resultSet.getLong(2));
            FriendRequest friendRequest = new FriendRequest(user, user2);
            friendRequestList.add((E) friendRequest);
        }
        return friendRequestList;
    }


}
