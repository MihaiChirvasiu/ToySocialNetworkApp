package com.company.Repository.Database;

import com.company.Domain.Entity;
import com.company.Domain.FriendRequest;
import com.company.Domain.Validators.Validator;
import com.company.Utils.STATUS;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    public void addFriendRequest(E friendRequest) throws SQLException
    {
        validator.validate(friendRequest);

        String sql = "insert into friendrequest (id_user1, id_user2 " +
                ", status ) values (?, ?, ?)";

        PreparedStatement ps = getStatement(sql);
        FriendRequest friendRequest1=(FriendRequest) friendRequest;

        ps.setLong(1, friendRequest1.getUser1().getId());
        ps.setLong(2, friendRequest1.getUser2().getId());
        ps.setString(3, friendRequest1.getStatus().toString());

        ps.executeUpdate();
    }

    public void updateStatus(E friendRequest, STATUS newStatus) throws IOException, SQLException{
        validator.validate(friendRequest);
        FriendRequest friendRequest1 = (FriendRequest) friendRequest;
        if(newStatus.equals(STATUS.REJECTED))
            friendRequest1.rejectRequest();
        else
            friendRequest1.acceptRequest();
        ID idUser1 = (ID) friendRequest1.getUser1().getId();
        ID idUser2 = (ID) friendRequest1.getUser2().getId();
        String sql = "update friendrequests set status = " + friendRequest1.getStatus() + " where id_user1 = " + idUser1 + " and id_user2 = " + idUser2 ;

        PreparedStatement ps = getStatement(sql);

        ps.executeUpdate();
    }


}
