package com.example.toysocialnetwork.Events;

import com.example.toysocialnetwork.Domain.Entity;
import com.example.toysocialnetwork.Domain.User;

import javax.xml.stream.events.EntityReference;

public class StatusEvent implements Event{
    private ExecutionStatusEventType type;
    private Entity<Long> entity;

    public StatusEvent(ExecutionStatusEventType type, Entity<Long> entity){
        this.type = type;
        this.entity = entity;
    }

    /**
     * Getter for the entity involved in the event
     * @return the entity
     */
    public Entity<Long> getEntity(){
        return entity;
    }

    /**
     * Setter for the entity
     * @param entity the entity to be set
     */
    public void setEntity(Entity<Long> entity){
        this.entity = entity;
    }

    /**
     * Getter for the type
     * @return the type of the event
     */
    public ExecutionStatusEventType getType(){
        return type;
    }

    /**
     * Setter for the event
     * @param type the type to be set
     */
    public void setType(ExecutionStatusEventType type){
        this.type = type;
    }
}
