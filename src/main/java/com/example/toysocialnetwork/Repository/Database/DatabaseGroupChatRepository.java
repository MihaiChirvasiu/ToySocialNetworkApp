package com.example.toysocialnetwork.Repository.Database;

import com.example.toysocialnetwork.Domain.Entity;
import com.example.toysocialnetwork.Domain.GroupChat;
import com.example.toysocialnetwork.Domain.PublicEvent;
import com.example.toysocialnetwork.Domain.User;
import com.example.toysocialnetwork.Repository.RepoException;
import com.example.toysocialnetwork.Repository.UserRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DatabaseGroupChatRepository <ID, E extends Entity<ID>, E1 extends Entity<ID>>{

    private String url;
    private String username;
    private String password;
    private UserRepository<ID, E1> databaseUserRepository;

    public DatabaseGroupChatRepository(String url, String username, String password, UserRepository<ID, E1> databaseUserRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.databaseUserRepository = databaseUserRepository;
    }

    PreparedStatement getStatement(String sql) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql);
        return ps;
    }

    private void generateID(GroupChat groupChat) throws SQLException {
        while(true){
            long randomID = ThreadLocalRandom.current().nextInt(0, 10001);
            String sql = "select id_group from group_chat where id_group = " + randomID;
            PreparedStatement ps = getStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            if(!resultSet.next()) {
                groupChat.setId(randomID);
                return;
            }
        }
    }

    private void generateCode(GroupChat groupChat) throws SQLException {
        while (true){
            StringBuilder joinCode = new StringBuilder();
            for(int i = 0; i < 8; i++){
                long randomID = ThreadLocalRandom.current().nextLong(97, 123);
                joinCode.append((char) randomID);
            }
            String sql = "select join_code from group_chat where join_code = '" + joinCode + "'";
            PreparedStatement ps = getStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            if(!resultSet.next()) {
                groupChat.setJoinCode(joinCode.toString());
                return;
            }
        }
    }

    public void addGroup(GroupChat groupChat) throws SQLException {

        generateID(groupChat);
        generateCode(groupChat);
        String sql = "insert into group_chat ( id_group, name, join_code ) values( ?, ?, ? )";
        PreparedStatement ps = getStatement(sql);
        ps.setLong(1,groupChat.getId());
        ps.setString(2, groupChat.getName());
        ps.setString(3, groupChat.getJoinCode());
        ps.executeUpdate();
    }

    public List<User> getUsersFromGroup(GroupChat groupChat) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "select id_user from group_users where id_group = " + groupChat.getId();
        PreparedStatement ps = getStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            Long id = resultSet.getLong(1);
            users.add((User) databaseUserRepository.findOne((ID)id));
        }
        return users;
    }

    public void joinGroup(User user, GroupChat groupChat) throws SQLException {
        List<User> users = getUsersFromGroup(groupChat);
        if(users.contains(user))
            throw new RepoException("User already in this group!");
        String sql = "insert into group_users ( id_group, id_user ) values( ?, ? )";
        PreparedStatement ps = getStatement(sql);
        ps.setLong(1, groupChat.getId());
        ps.setLong(2,user.getId());
        ps.executeUpdate();
    }

    public void leaveGroup(User user, GroupChat groupChat) throws SQLException {
        List<User> users = getUsersFromGroup(groupChat);
        if(!users.contains(user))
            throw new RepoException("User is not in this group!");
        String sql = "delete from group_users where id_user = " + user.getId() + " and id_group = " + groupChat.getId();
        PreparedStatement ps = getStatement(sql);
        ps.executeUpdate();
    }

    public GroupChat getGroupByIDGroup(ID idGroup) throws SQLException{
        String sql = "select * from group_chat where id_group = " + idGroup;
        PreparedStatement ps = getStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        if(resultSet.next()){
            Long id = resultSet.getLong(1);
            GroupChat groupChat = new GroupChat(resultSet.getString(2));
            groupChat.setId(id);
            groupChat.setJoinCode(resultSet.getString(3));
            return groupChat;
        }
        return null;
    }

    public GroupChat getGroupByJoinCode(String joinCode) throws SQLException{
        String sql = "select * from group_chat where join_code = '" + joinCode + "'";
        PreparedStatement ps = getStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        if(resultSet.next()){
            Long id = resultSet.getLong(1);
            GroupChat groupChat = new GroupChat(resultSet.getString(2));
            groupChat.setId(id);
            groupChat.setJoinCode(resultSet.getString(3));
            return groupChat;
        }
        return null;
    }

    public List<GroupChat> getGroupChatByIDUser(ID idUser) throws SQLException{
        List<GroupChat> groups = new ArrayList<>();
        String sql = "select id_group from group_users where id_user = " + idUser;
        PreparedStatement ps = getStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            Long idGroup = resultSet.getLong(1);
            var groupChat = getGroupByIDGroup((ID) idGroup);
            groups.add(groupChat);
        }
        return groups;
    }


}
