package com.example.toysocialnetwork.Domain;

public class GroupChat extends Entity<Long>{

    private String name;
    private String joinCode;

    /**
     * Setter for the joinCode
     * @param joinCode The code to be set
     */
    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    /**
     * Getter for joinCode
     * @return the joinCode
     */
    public String getJoinCode() {
        return joinCode;
    }

    /**
     * Constructor for GroupChat
     * @param name The name of the GroupChat
     */
    public GroupChat(String name){
        this.name = name;
    }

    /**
     * Getter for name of the groupChat
     * @return the name of the groupChat
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name
     * @param name Name to be set
     */
    public void setName(String name) {
        this.name = name;
    }
}
