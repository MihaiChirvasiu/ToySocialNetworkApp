package com.example.toysocialnetwork.Observer;


import com.example.toysocialnetwork.Events.Event;

import java.io.IOException;
import java.sql.SQLException;

public interface Observer<E extends Event> {
    void update(E e) throws SQLException, IOException;
}
