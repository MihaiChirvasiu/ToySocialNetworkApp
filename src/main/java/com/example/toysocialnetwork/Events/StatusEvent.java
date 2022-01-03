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

    public Entity<Long> getEntity(){
        return entity;
    }

    public void setEntity(Entity<Long> entity){
        this.entity = entity;
    }

    public ExecutionStatusEventType getType(){
        return type;
    }

    public void setType(ExecutionStatusEventType type){
        this.type = type;
    }
}
