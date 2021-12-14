package com.example.toysocialnetwork.Domain;

import com.example.toysocialnetwork.Utils.STATUS;

import java.time.LocalDateTime;

public class FriendRequestDTO {
    private String firstName;
    private String lastName;
    private STATUS status;
    private LocalDateTime date;
    private Long id;

    public FriendRequestDTO(FriendRequest friendRequest)
    {
        this.firstName=friendRequest.getUser2().getFirstName();
        this.lastName=friendRequest.getUser2().getLastName();
        this.status=friendRequest.getStatus();
        this.date=friendRequest.getDate();
        this.id=friendRequest.getUser2().getId();
    }

    public String getFirstName() {
        return firstName;
    }

    public Long getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public STATUS getStatus() {
        return status;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
