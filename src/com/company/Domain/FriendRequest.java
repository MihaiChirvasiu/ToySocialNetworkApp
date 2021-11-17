package com.company.Domain;

import com.company.Utils.STATUS;

public class FriendRequest extends Entity<Long> {

    private User user1;
    private User user2;
    private STATUS status;

    public FriendRequest(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.status = STATUS.PENDING;
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
     * Setter for updating the friendRequest Status
     * to APPROVED
     */
    public void acceptRequest()
    {
        this.status=STATUS.APPROVED;
    }

    /**
     * Setter for updating the friendRequest Status
     * to REJECTED
     */
    public void rejectRequest()
    {
        this.status=STATUS.REJECTED;
    }
}
