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

    public User getUser1()
    {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public STATUS getStatus() {
        return status;
    }

    public void acceptRequest()
    {
        this.status=STATUS.APPROVED;
    }

    public void rejectRequest()
    {
        this.status=STATUS.REJECTED;
    }
}
