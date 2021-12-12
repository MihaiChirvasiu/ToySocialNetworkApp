package com.example.toysocialnetwork.Domain;

import com.example.toysocialnetwork.Utils.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Message extends Entity<Long> {

    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime date;
    private Message replyMessage;

    public Message(User from, String message, LocalDateTime date){
        this.from = from;
        this.message = message;
        this.to = new ArrayList<>();
        this.date = date;
        this.replyMessage = null;
    }

    /**
     * Setter(adder) for the toList of users
     * @param user A user that will receive the message
     */
    public void setToUsers(User user){
        to.add(user);
    }

    /**
     * Getter for the sender User
     * @return The user that sent the message
     */
    public User getFromUser(){
        return from;
    }

    /**
     * Getter for the list of receivers
     * @return The list of users the message was sent to
     */
    public List<User> getToUsers(){
        return to;
    }

    /**
     * Getter for the message
     * @return The message that was sent
     */
    public String getMessage(){
        return message;
    }

    /**
     * Getter for the date of the message
     * @return The date when the message was sent
     */
    public LocalDateTime getDate(){
        return date;
    }

    /**
     * Setter for the reply message
     * @param message The message to be set
     */
    public void setReplyMessage(Message message){
        this.replyMessage = message;
    }

    /**
     * Getter for the reply message
     * @return The message that is a reply
     */
    public Message getReplyMessage(){
        return replyMessage;
    }

}
