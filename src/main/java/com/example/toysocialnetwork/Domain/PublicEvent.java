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

    public String getNameEvent() {
        return nameEvent;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }
}
