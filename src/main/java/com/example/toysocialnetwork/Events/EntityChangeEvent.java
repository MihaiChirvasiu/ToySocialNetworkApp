package com.example.toysocialnetwork.Events;

import com.example.toysocialnetwork.Domain.Entity;
import com.example.toysocialnetwork.Domain.User;

public class EntityChangeEvent implements Event{
    private ChangeEventType type;
    private Entity<Long> data, oldData;

    public EntityChangeEvent(ChangeEventType type, Entity<Long> data){
        this.type = type;
        this.data = data;
    }

    public EntityChangeEvent(ChangeEventType type, Entity<Long> data, Entity<Long> oldData){
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    /**
     * Getter for the event type
     * @return the type of the event
     */
    public ChangeEventType getType(){
        return type;
    }

    /**
     * Getter for the data
     * @return the data of the entity
     */
    public Entity<Long> getData(){
        return data;
    }

    /**
     * Getter for the old data
     * @return the old data of the event
     */
    public Entity<Long> getOldData(){
        return oldData;
    }
}
