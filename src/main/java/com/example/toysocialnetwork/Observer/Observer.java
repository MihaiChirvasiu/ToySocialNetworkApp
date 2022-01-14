package com.example.toysocialnetwork.Observer;


import com.example.toysocialnetwork.Events.Event;

import java.io.IOException;
import java.sql.SQLException;

public interface Observer<E extends Event> {
    /**
     * After the observer is notified we need to update it(see the changes)
     * @param e The event that happened
     * @throws SQLException database
     * @throws IOException file
     */
    void update(E e) throws SQLException, IOException;
}
