package com.company.Repository;

import com.company.Domain.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface FriendshipRepository<ID, E> {

    void addFriendshipRepo(E friendship) throws IOException, SQLException;

    void deleteFriendshipRepo(E friendship) throws IOException, SQLException;

    List<E> findAllFriendships() throws SQLException;

    E findOneFriendship(E friendship) throws SQLException;

    void updateFriendship(E oldFriendship, E newFriendship) throws IOException, SQLException;

    int getSize() throws SQLException;

}
