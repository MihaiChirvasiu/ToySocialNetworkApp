package com.example.toysocialnetwork.Repository.Database;

import com.example.toysocialnetwork.Domain.Entity;
import com.example.toysocialnetwork.Domain.GroupChat;
import com.example.toysocialnetwork.Domain.Message;
import com.example.toysocialnetwork.Domain.User;
import com.example.toysocialnetwork.Domain.Validators.Validator;
import com.example.toysocialnetwork.Paging.Page;
import com.example.toysocialnetwork.Paging.Pageable;
import com.example.toysocialnetwork.Paging.Paginator;
import com.example.toysocialnetwork.Paging.PagingRepository;
import com.example.toysocialnetwork.Repository.UserRepository;

import java.lang.reflect.Member;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DatabaseMessageRepository<ID, E extends Entity<ID>, E1 extends Entity<ID>, E2 extends Entity<ID>> {

    String url;
    String username;
    String password;
    Validator<E> validator;
    private UserRepository<ID, E1> databaseUserRepository;
    private DatabaseGroupChatRepository<ID, E2, E1> databaseGroupRepository;

    public DatabaseMessageRepository(String url, String username, String password, Validator<E> validator, UserRepository<ID, E1> databaseUserRepository,
                                     DatabaseGroupChatRepository<ID, E2, E1>  databaseGroupRepository){
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.databaseUserRepository = databaseUserRepository;
        this.databaseGroupRepository = databaseGroupRepository;
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

    private void generateID(Message msg) throws SQLException {
        while(true){
            long randomID = ThreadLocalRandom.current().nextInt(0, 10001);
            String sql = "select id_message from messages where id_message = " + randomID;
            PreparedStatement ps = getStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            if(!resultSet.next()) {
                msg.setId(randomID);
                return;
            }
        }
    }

    public void setMessageGroup(E message, E2 group){
        Message message1 = (Message) message;
        message1.setGroupChat((GroupChat) group);
    }

    /**
     *
     * @param message The message to be added in the database
     * @param toUser The user the message is addressed to
     * @throws SQLException if the command is not a valid one
     */
    public void addMessage(E message, List<User> toUser) throws SQLException{

        validator.validate(message);
        String sql = "insert into messages (id_message, id_user_from, message, date, reply, id_group ) values(?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = getStatement(sql);

        Message message1 = (Message) message;
        generateID(message1);
        ps.setLong(1, message1.getId());
        ps.setLong(2, message1.getFromUser().getId());
        ps.setString(3, message1.getMessage());
        ps.setString(4, message1.getDate().toString());
        try {
            ps.setLong(5, message1.getReplyMessage().getId());
        }
        catch (NullPointerException e){
            ps.setLong(5, -1);
        }
        try {
            ps.setLong(6, message1.getGroupChat().getId());
        }
        catch (NullPointerException e){
            ps.setLong(6, -1);
        }
        ps.executeUpdate();
        addMessageUsersTo((E) message1, toUser);

    }

    private void addMessageUsersTo(E message, List<User> users) throws SQLException{
        for(int i = 0; i < users.size(); i++) {
            String sql = "insert into message_to_users (id_message, id_to ) values(?, ?)";

            PreparedStatement ps = getStatement(sql);

            ps.setLong(1, (Long) message.getId());
            ps.setLong(2, (Long) users.get(i).getId());
            ps.executeUpdate();
        }



    }

    public List<ID> getUsersFromIDMessage(Long id) throws SQLException {
        List<Long> idList = new ArrayList<>();
        String sql = "select id_to from message_to_users where id_message = " + id;
        String sql1 = "select id_user_from from messages where id_message = " + id;
        PreparedStatement ps = getStatement(sql);
        PreparedStatement ps1 = getStatement(sql1);
        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            idList.add(resultSet.getLong(1));
        }
        ResultSet resultSet1 = ps1.executeQuery();
        if(resultSet1.next())
            idList.add(resultSet1.getLong(1));
        return (List<ID>) idList;
    }

    /**
     *
     * @param id The date to be searched
     * @return The message with the specified date or null if it doesn't exist
     * @throws SQLException if the command is not a valid one
     */
    public E findOneMessage(ID id) throws SQLException{
        String sql = "select * from messages where id_message = " + id;
        List<ID> idList = getUsersFromIDMessage((Long) id);
        PreparedStatement ps = getStatement(sql);

        ResultSet resultSet = ps.executeQuery();
        if(resultSet.next()) {
            Long idUser = resultSet.getLong(2);
            Message message = new Message((User) databaseUserRepository.findOne((ID) idUser), resultSet.getString(3), LocalDateTime.parse(resultSet.getString(4)));
            message.setId(resultSet.getLong(1));
            if (!Objects.equals(resultSet.getLong(5), -1)) {
                Long idUserTo = resultSet.getLong(5);
                message.setReplyMessage((Message) findOneMessage((ID) idUserTo));
            }
            if(!Objects.equals(resultSet.getLong(6), -1)) {
                Long idGroup = resultSet.getLong(6);
                message.setGroupChat(databaseGroupRepository.getGroupByIDGroup((ID)idGroup));
            }
            for(int i = 0; i < idList.size(); i++){
                Long idToUser = (Long) idList.get(i);
                message.setToUsers((User) databaseUserRepository.findOne((ID) idToUser));
            }
            return (E) message;
        }
        return null;
    }

    private List<ID> idMessageConversation(E1 idUser1, E1 idUser2) throws SQLException {
        List<Long> idList = new ArrayList<>();
        String sql = "select id_message from message_to_users where id_to = " + idUser2.getId() + " or id_to = " + idUser1.getId();
        PreparedStatement ps = getStatement(sql);

        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            idList.add(resultSet.getLong(1));
        }
        return (List<ID>) idList;

    }

    /**
     *
     * @param idUser1 The first User
     * @param idUser2 The second User
     * @return The conversation between the two users
     * @throws SQLException if the command is not a valid one
     */
    public List<E> getConversation(E1 idUser1, E1 idUser2) throws SQLException{
        List<ID> idMessageConv = idMessageConversation(idUser1, idUser2);
        List<E> messageList = new ArrayList<>();
        String sql = "select * from messages where (id_user_from = " + idUser1.getId() + " and id_group = -1)" +
                " or (id_user_from = " + idUser2.getId() +  " and id_group = -1) order by date";

        PreparedStatement ps = getStatement(sql);

        ResultSet resultSet = ps.executeQuery();

        while(resultSet.next()){
            for(int i = 0; i < idMessageConv.size(); i++)
                if(resultSet.getLong(1) == (Long) idMessageConv.get(i)) {
                    Long id = resultSet.getLong(2);
                    if(idUser1.getId().equals(id)) {
                        Message message = new Message((User) idUser1, resultSet.getString(3), LocalDateTime.parse(resultSet.getString(4)));
                        message.setToUsers((User)idUser2);
                        message.setId(resultSet.getLong(1));
                        if (!Objects.equals(resultSet.getLong(5), -1)) {
                            Long idUserTo = resultSet.getLong(5);
                            message.setReplyMessage((Message) findOneMessage((ID) idUserTo));
                        }

                        messageList.add((E) message);
                    }
                    else{
                        Message message = new Message((User) idUser2, resultSet.getString(3), LocalDateTime.parse(resultSet.getString(4)));
                        message.setToUsers((User) idUser1);
                        message.setId(resultSet.getLong(1));
                        if (!Objects.equals(resultSet.getLong(5), -1)) {
                            Long idUserTo = resultSet.getLong(5);
                            message.setReplyMessage((Message) findOneMessage((ID) idUserTo));
                        }
                        messageList.add((E) message);
                    }
                }
        }
        return messageList;
    }

    public List<E> getConversationGroup(E1 idUser1, E2 groupChat) throws SQLException{
        Map<E1, List<ID>> mapConv = new HashMap<>();
        List<E1> idUsersTo = (List<E1>) databaseGroupRepository.getUsersFromGroup((GroupChat) groupChat);
        if(idUsersTo == null)
            return null;
        for(int i = 0; i < idUsersTo.size(); i++){
            mapConv.put(idUsersTo.get(i), idMessageConversation(idUser1, idUsersTo.get(i)));
        }
        List<E> messageList = new ArrayList<>();
        String sql = "select * from messages where id_group = " + groupChat.getId()+ " order by date";

        PreparedStatement ps = getStatement(sql);

        ResultSet resultSet = ps.executeQuery();

        while(resultSet.next()) {
            for (var K : mapConv.keySet())
                for (int j = 0; j < mapConv.get(K).size(); j++) {
                    if (resultSet.getLong(1) == (Long) mapConv.get(K).get(j)) {
                        List<E1> idUsersToCopy = idUsersTo;
                        Long id = resultSet.getLong(2);
                        if (databaseGroupRepository.getGroupChatByIDUser((ID)id) != null) {
                            Message message = new Message((User) databaseUserRepository.findOne((ID) id), resultSet.getString(3), LocalDateTime.parse(resultSet.getString(4)));
                            idUsersToCopy.remove(databaseUserRepository.findOne((ID) id));
                            message.setToUsers((User) idUsersToCopy);
                            message.setId(resultSet.getLong(1));
                            if (!Objects.equals(resultSet.getLong(5), -1)) {
                                Long idUserTo = resultSet.getLong(5);
                                message.setReplyMessage((Message) findOneMessage((ID) idUserTo));
                            }
                            Long idGroup = resultSet.getLong(6);
                            message.setGroupChat(databaseGroupRepository.getGroupByIDGroup((ID) idGroup));

                            messageList.add((E) message);
                        }
                    }
                }
        }
        return messageList;
    }

    public List<E> getAllConversation(E1 idUser1) throws SQLException{
        List<E> messageList = new ArrayList<>();
        String sql = "select * from messages where id_user_from = " + idUser1.getId() + " order by date";
        PreparedStatement ps = getStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            Message message = new Message((User) idUser1, resultSet.getString(3), LocalDateTime.parse(resultSet.getString(4)));
            messageList.add((E) message);
        }
        return messageList;
    }

    /**
     *
     * @param message The message the reply will be sent to
     * @param reply The reply to the message
     * @throws SQLException if the command is not a valid one
     */
    public void setReplyMessage(E message, E reply) throws SQLException {
        Message replyMessage =(Message) reply;
        Message message1 = (Message) message;
        String sql = "update messages set reply = " + replyMessage.getId() + " where id_message = " + message1.getId();

        PreparedStatement ps = getStatement(sql);
        ps.executeUpdate();
    }

}
