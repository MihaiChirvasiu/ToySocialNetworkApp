package com.company.Repository.Database;

import com.company.Domain.Entity;
import com.company.Domain.Message;
import com.company.Domain.User;
import com.company.Domain.Validators.Validator;
import com.company.Repository.UserRepository;

import java.lang.reflect.Member;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DatabaseMessageRepository<ID, E extends Entity<ID>, E1 extends Entity<ID>> {

    String url;
    String username;
    String password;
    Validator<E> validator;
    private UserRepository<ID, E1> databaseUserRepository;

    public DatabaseMessageRepository(String url, String username, String password, Validator<E> validator, UserRepository<ID, E1> databaseUserRepository){
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.databaseUserRepository = databaseUserRepository;
    }

    PreparedStatement getStatement(String sql) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql);
        return ps;
    }

    public void addMessage(E message, User toUser) throws SQLException{

        validator.validate(message);
        String sql = "insert into messages (id_user_from, id_user_to, message, date, reply ) values(?, ?, ?, ?, ?)";
        PreparedStatement ps = getStatement(sql);

        Message message1 = (Message) message;
        ps.setLong(1, message1.getFromUser().getId());
        ps.setLong(2, toUser.getId());
        ps.setString(3, message1.getMessage());
        ps.setString(4, message1.getDate().toString());
        try {
            ps.setString(5, message1.getReplyMessage().getDate().toString());
        }
        catch (NullPointerException e){
            ps.setString(5, "null");
        }

        ps.executeUpdate();
    }

    public E findOneMessage(LocalDateTime date) throws SQLException{
        String sql = "select * from messages where date = '" + date.toString() + "'";

        PreparedStatement ps = getStatement(sql);

        ResultSet resultSet = ps.executeQuery();
        if(resultSet.next()) {
            Long idUser = resultSet.getLong(1);
            Message message = new Message((User) databaseUserRepository.findOne((ID) idUser), resultSet.getString(3), LocalDateTime.parse(resultSet.getString(4)));
            if (!Objects.equals(resultSet.getString(5), "null"))
                message.setReplyMessage((Message) findOneMessage(LocalDateTime.parse(resultSet.getString(5))));
            while (true) {
                Long idToUser = resultSet.getLong(2);
                message.setToUsers((User) databaseUserRepository.findOne((ID) idToUser));
                if(!resultSet.next())
                    break;
            }
            return (E) message;
        }
        return null;
    }

    public List<E> getConversation(E1 idUser1, E1 idUser2) throws SQLException{
        List<E> messageList = new ArrayList<>();
        String sql = "select * from messages where (id_user_from = " + idUser1.getId() + " and id_user_to = " + idUser2.getId() +
                " ) or (id_user_from = " + idUser2.getId() + " and id_user_to = " + idUser1.getId() + " ) order by date";

        PreparedStatement ps = getStatement(sql);

        ResultSet resultSet = ps.executeQuery();

        while(resultSet.next()){
            Message message = new Message((User) idUser1, resultSet.getString(3), LocalDateTime.parse(resultSet.getString(4)));
            message.setToUsers((User) idUser1);
            if (!Objects.equals(resultSet.getString(5), "null"))
                message.setReplyMessage((Message) findOneMessage(LocalDateTime.parse(resultSet.getString(5))));
            messageList.add((E) message);
        }
        return messageList;
    }
}
