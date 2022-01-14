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

    /**
     * Generates a random ID for the groupChat
     * @param groupChat the groupChat that will receive a random ID
     * @throws SQLException database
     */
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

    /**
     * Generates a random access code
     * @param groupChat the groupChat that will have the joinCode
     * @throws SQLException
     */
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

    /**
     *
     * @param groupChat that will be added in the database
     * @throws SQLException database
     */
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

    /**
     *
     * @param groupChat The groupChat of which all users will be returned
     * @return a list of all users from the groupChat
     * @throws SQLException database
     */
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

    /**
     *
     * @param user The user that wants to join a group
     * @param groupChat the groupChat that the user will join
     * @throws SQLException database
     */
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

    /**
     *
     * @param user The user that will leave the group
     * @param groupChat the groupChat that the user wants to leave
     * @throws SQLException database
     */
    public void leaveGroup(User user, GroupChat groupChat) throws SQLException {
        List<User> users = getUsersFromGroup(groupChat);
        if(!users.contains(user))
            throw new RepoException("User is not in this group!");
        String sql = "delete from group_users where id_user = " + user.getId() + " and id_group = " + groupChat.getId();
        PreparedStatement ps = getStatement(sql);
        ps.executeUpdate();
    }

    /**
     *
     * @param idGroup the ID of the group that we search for
     * @return the groupChat or null otherwise
     * @throws SQLException database
     */
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

    /**
     *
     * @param joinCode The joinCode for the groupChat
     * @return the groupChat or null otherwise
     * @throws SQLException database
     */
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

    /**
     *
     * @param idUser the ID of the User
     * @return a list of groupChats that the user is joined
     * @throws SQLException database
     */
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
