package com.example.toysocialnetwork.Domain;

public class GroupChat extends Entity<Long>{

    private String name;
    private String joinCode;

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public GroupChat(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
