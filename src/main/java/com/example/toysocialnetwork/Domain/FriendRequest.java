package com.example.toysocialnetwork.Domain;

import com.example.toysocialnetwork.Utils.STATUS;

import java.time.LocalDateTime;

public class FriendRequest extends Entity<Long> {

    private User user1;
    private User user2;
    private STATUS status;
    private LocalDateTime date;

    public FriendRequest(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.status = STATUS.pending;
    }

    /**
     *
     * @return The user that sent the friendRequest
     */
    public User getUser1() {
        return user1;
    }

    /**
     * Getter for User
     * @return The user that received the friendRequest
     */
    public User getUser2() {
        return user2;
    }

    /**
     * Getter for Status
     * @return The Status of the friendRequest
     */
    public STATUS getStatus() {
        return status;
    }

    /**
     * Setter for the date
     * @param date The date to be set
     */
    public void setDate(LocalDateTime date)
    {
        this.date=date;
    }

    /**
     * Getter for the date
     * @return The date
     */
    public LocalDateTime getDate()
    {
        return this.date;
    }

    /**
     * Setter to status
     */
    public void setStatus()
    {
        status=STATUS.pending;
    }

    /**
     * Setter for updating the friendRequest Status
     * to APPROVED
     */
    public void acceptRequest()
    {
        this.status=STATUS.approved;
    }

    /**
     * Setter for updating the friendRequest Status
     * to REJECTED
     */
    public void rejectRequest()
    {
        this.status=STATUS.rejected;
    }
}
