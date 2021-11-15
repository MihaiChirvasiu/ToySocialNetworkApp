package com.company.Domain;

public class Friendship extends Entity<Long> {

    private User user1;
    private User user2;

    public Friendship(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
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
