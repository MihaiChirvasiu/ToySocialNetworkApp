package com.example.toysocialnetwork.Repository;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Set;

import com.example.toysocialnetwork.Domain.Entity;
import com.example.toysocialnetwork.Domain.User;
import com.example.toysocialnetwork.Domain.Validators.ValidationException;
import com.example.toysocialnetwork.Paging.Page;
import com.example.toysocialnetwork.Paging.Pageable;

public interface UserRepository<ID, E extends Entity<ID>> {

    /**
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     * or null - if there is no entity with the given id
     * @throws IllegalArgumentException if id is null.
     */
    E findOne(ID id) throws SQLException;

    /**
     * @return all entities
     */
    Iterable<E> findAll() throws SQLException;

    /**
     * @param entity entity must be not null
     * @return null- if the given entity is saved
     * otherwise returns the entity (id already exists)
     * @throws ValidationException      if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null.     *
     */
    E save(E entity) throws IOException, SQLException;


    /**
     * removes the entity with the specified id
     *
     * @param id id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws IllegalArgumentException if the given id is null.
     */
    E delete(ID id) throws IOException, SQLException;

    /**
     * @param entity entity must not be null
     * @return null - if the entity is updated,
     * otherwise  returns the entity  - (e.g id does not exist).
     * @throws IllegalArgumentException if the given entity is null.
     * @throws ValidationException      if the entity is not valid.
     */
    E update(E entity) throws IOException, SQLException;

    int getSize() throws SQLException;

    Set<ID> getKeys() throws SQLException;

    String findEmail(String email) throws SQLException;

    User loginRepo(String email, String password) throws SQLException;

    Page<E> findAllPage(Pageable pageable) throws SQLException;
}
