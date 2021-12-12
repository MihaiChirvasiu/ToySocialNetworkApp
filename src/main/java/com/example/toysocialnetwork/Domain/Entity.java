package com.example.toysocialnetwork.Domain;

import java.io.Serializable;

public class Entity<ID> implements Serializable {

    private static final long serialVersionUID = 7331115341259248461L;
    private ID id;

    /**
     *
     * @return The id of the entity
     */
    public ID getId() {
        return id;
    }

    /**
     *
     * @param id The id specified to be set
     */
    public void setId(ID id) {
        this.id = id;
    }
}