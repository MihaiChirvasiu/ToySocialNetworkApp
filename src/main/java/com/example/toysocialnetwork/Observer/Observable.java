package com.example.toysocialnetwork.Observer;

import com.example.toysocialnetwork.Events.Event;

import java.sql.SQLException;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t) throws SQLException;
}
