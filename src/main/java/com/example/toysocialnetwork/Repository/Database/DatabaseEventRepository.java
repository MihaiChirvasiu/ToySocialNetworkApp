package com.example.toysocialnetwork.Repository.Database;

import com.example.toysocialnetwork.Domain.Entity;
import com.example.toysocialnetwork.Domain.Message;
import com.example.toysocialnetwork.Domain.PublicEvent;
import com.example.toysocialnetwork.Domain.User;
import com.example.toysocialnetwork.Repository.RepoException;
import com.example.toysocialnetwork.Repository.UserRepository;
import com.example.toysocialnetwork.Utils.ComparatorForDate;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DatabaseEventRepository<ID, E extends Entity<ID>, E1 extends Entity<ID>> {

    private String url;
    private String username;
    private String password;
    private UserRepository<ID, E1> databaseUserRepository;

    public DatabaseEventRepository(String url, String username, String password, UserRepository<ID, E1> databaseUserRepository) {
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

    private void generateID(PublicEvent event) throws SQLException {
        while(true){
            long randomID = ThreadLocalRandom.current().nextInt(0, 10001);
            String sql = "select id_event from events where id_event = " + randomID;
            PreparedStatement ps = getStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            if(!resultSet.next()) {
                event.setId(randomID);
                return;
            }
        }
    }

    public void addEvent(PublicEvent event) throws SQLException {

        generateID(event);
        String sql = "insert into events ( id_event, name, date ) values( ?, ?, ? )";
        PreparedStatement ps = getStatement(sql);
        ps.setLong(1,event.getId());
        ps.setString(2, event.getNameEvent());
        ps.setString(3, event.getEventDate().toString());
        ps.executeUpdate();
    }

    public List<User> getUsersSubscribed(PublicEvent event) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "select id_user from users_subscribed where id_event = " + event.getId();
        PreparedStatement ps = getStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            Long id = resultSet.getLong(1);
            users.add((User) databaseUserRepository.findOne((ID)id));
        }
        return users;
    }

    public void subscribeToEvent(User user,PublicEvent event) throws SQLException {
        List<User> users = getUsersSubscribed(event);
        if(users.contains(user))
            throw new RepoException("User already subscribed to this event!");
        String sql = "insert into users_subscribed ( id_event, id_user ) values( ?, ? )";
        PreparedStatement ps = getStatement(sql);
        ps.setLong(1,event.getId());
        ps.setLong(2,user.getId());
        ps.executeUpdate();
    }

    public void unsubscribeFromEvent(User user,PublicEvent event) throws SQLException {
        List<User> users = getUsersSubscribed(event);
        if(!users.contains(user))
            throw new RepoException("User is not subscribed to this event!");
        String sql = "delete from users_subscribed where id_user = " + user.getId() + " and id_event = " + event.getId();
        PreparedStatement ps = getStatement(sql);
        ps.executeUpdate();
    }

    public PublicEvent getEventByIDEvent(ID idEvent) throws SQLException{
        String sql = "select * from events where id_event = " + idEvent;
        PreparedStatement ps = getStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        if(resultSet.next()){
            Long id = resultSet.getLong(1);
            PublicEvent event = new PublicEvent(resultSet.getString(2),LocalDateTime.parse(resultSet.getString(3)));
            event.setId(id);
            return event;
        }
        return null;
    }

    public List<PublicEvent> getEventByIDUser(ID idUser) throws SQLException{
        List<PublicEvent> events = new ArrayList<>();
        String sql = "select id_event from users_subscribed where id_user = " + idUser;
        PreparedStatement ps = getStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            Long idEvent = resultSet.getLong(1);
            var event = getEventByIDEvent((ID) idEvent);
            events.add(event);
        }
        return events;
    }

    public List<PublicEvent> getEventByIDUserOrderByDate(ID idUser) throws SQLException{
        List<PublicEvent> events = new ArrayList<>();
        List<PublicEvent> eventsByDate = new ArrayList<>();
        String sql = "select id_event from users_subscribed where id_user = " + idUser;
        PreparedStatement ps = getStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            Long idEvent = resultSet.getLong(1);
            var event = getEventByIDEvent((ID) idEvent);
            events.add(event);
        }
        for(int i=0;i<events.size();i++)
            if(events.get(i).getEventDate().isBefore(LocalDateTime.now()))
                events.remove(i);
        Collections.sort(events, new ComparatorForDate());
        return events;
    }

    public List<PublicEvent> getPublicEvents() throws SQLException {
        List<PublicEvent> events = new ArrayList<>();
        String sql = "select * from events ";
        PreparedStatement ps = getStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        while(resultSet.next()){
            Long id = resultSet.getLong(1);
            PublicEvent event = new PublicEvent(resultSet.getString(2),LocalDateTime.parse(resultSet.getString(3)));
            event.setId(id);
            events.add(event);
        }
        return events;
    }
}
