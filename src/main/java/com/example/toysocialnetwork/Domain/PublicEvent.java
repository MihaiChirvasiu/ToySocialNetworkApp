package com.example.toysocialnetwork.Domain;

import java.time.LocalDateTime;


public class PublicEvent extends Entity<Long> {

    String nameEvent;
    LocalDateTime eventDate;

    public PublicEvent(String name,LocalDateTime date)
    {
        this.nameEvent=name;
        this.eventDate=date;
    }

    /**
     * Getter for the name of the event
     * @return the name of the event
     */
    public String getNameEvent() {
        return nameEvent;
    }

    /**
     * Getter for the date of the event
     * @return the date of the event
     */
    public LocalDateTime getEventDate() {
        return eventDate;
    }
}
