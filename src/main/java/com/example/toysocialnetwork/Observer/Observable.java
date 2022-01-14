package com.example.toysocialnetwork.Observer;

import com.example.toysocialnetwork.Events.Event;

import java.sql.SQLException;

public interface Observable<E extends Event> {
    /**
     * Adds an observer
     * @param e the observer to be added
     */
    void addObserver(Observer<E> e);

    /**
     * Removes an observer
     * @param e the observer to be removed
     */
    void removeObserver(Observer<E> e);

    /**
     * Notifies an observer that a change occurred
     * @param t the event that occurred
     * @throws SQLException database
     */
    void notifyObservers(E t) throws SQLException;
}
