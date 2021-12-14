package com.example.toysocialnetwork.Domain;

import com.example.toysocialnetwork.Utils.STATUS;

import java.time.LocalDateTime;

public class FriendRequestDTO {
    private String firstName;
    private String lastName;
    private STATUS status;
    private LocalDateTime date;
    private Long id;

    public FriendRequestDTO(FriendRequest friendRequest) {
        this.firstName=friendRequest.getUser2().getFirstName();
        this.lastName=friendRequest.getUser2().getLastName();
        this.status=friendRequest.getStatus();
        this.date=friendRequest.getDate();
        this.id=friendRequest.getUser2().getId();
    }

    /**
     * Getter for FirstName
     * @return FirstName of the User
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Getter for ID
     * @return ID of the User
     */
    public Long getId() {
        return id;
    }

    /**
     * Getter for LastName
     * @return LastName of the User
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Getter for Status
     * @return STATUS of the friendRequest
     */
    public STATUS getStatus() {
        return status;
    }

    /**
     * Getter for Date
     * @return Date of the friendRequest
     */
    public LocalDateTime getDate() {
        return date;
    }
}
