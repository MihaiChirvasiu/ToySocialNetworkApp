package com.company.Domain;

import com.company.Utils.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Message extends Entity<Long> {

    private Long id;
    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime date;

    public Message(User from, String message, LocalDateTime date){
        this.from = from;
        this.message = message;
        this.to = new ArrayList<>();
        this.date = date;
    }

    /**
     * Setter(adder) for the toList of users
     * @param user A user that will receive the message
     */
    public void setToUsers(User user){
        to.add(user);
    }

    public User getFromUser(){
        return from;
    }

    public List<User> getToUsers(){
        return to;
    }

    public String getMessage(){
        return message;
    }

    public LocalDateTime getDate(){
        return date;
    }

}
