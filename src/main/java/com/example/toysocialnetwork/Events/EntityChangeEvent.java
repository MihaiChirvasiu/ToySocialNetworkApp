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

    public ChangeEventType getType(){
        return type;
    }

    public Entity<Long> getData(){
        return data;
    }

    public Entity<Long> getOldData(){
        return oldData;
    }
}
