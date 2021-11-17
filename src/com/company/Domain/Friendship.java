package com.company.Domain;

import com.company.Utils.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Friendship extends Entity<Long> {

    private User user1;
    private User user2;
    private LocalDateTime date;

    public Friendship(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    /**
     * Getter for date
     * @return Returns the date
     */
    public LocalDateTime getDate(){
        return this.date;
    }

    /**
     * Setter for date
     * @param setDate The date to be setted
     */
    public void setDate(LocalDateTime setDate){
        this.date = setDate;
    }

    /**
     * Getter for the first user
     * @return The first user of the friendship
     */
    public User getFirstUser() {
        return this.user1;
    }

    /**
     * Getter for the second user
     * @return The second user of the friendship
     */
    public User getSecondUser() {
        return this.user2;
    }

    /**
     * Setter for the first user
     * @param newFirstUser The new first user of the friendship
     */
    public void setFirstUser(User newFirstUser){
        this.user1 = newFirstUser;
    }

    /**
     * Setter for the second user
     * @param newSecondUser The second new user of the friendship
     */
    public void setSecondUser(User newSecondUser){
        this.user2 = newSecondUser;
    }

}
