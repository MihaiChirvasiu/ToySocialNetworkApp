package com.company.Repository.Memory;

import com.company.Domain.Entity;
import com.company.Domain.Validators.Validator;
import com.company.Repository.RepoException;
import com.company.Repository.UserRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class InMemoryRepositoryUser<ID, E extends Entity<ID>> implements UserRepository<ID,E> {

    private Validator<E> validator;
    private Map<ID,E> entities;

    public InMemoryRepositoryUser(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    /**
     * Throws RepoException if the id is null
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return The entity with the specified id
     */
    @Override
    public E findOne(ID id){
        if (id==null)
            throw new RepoException("Id must be not null");
        return entities.get(id);
    }

    /**
     *
     * @return All the existing users
     */
    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    /**
     *
     * @return The size of the repository
     */
    public int getSize(){
        return entities.size();
    }

    /**
     *
     * @return The set of ids that are associated with the users
     */
    public Set<ID> getKeys(){
        return entities.keySet();
    }

    /**
     * Throws RepoException if the entity is null
     * @param entity The User that we want to save
     *         entity must be not null
     * @return The entity if one is already existing in the repository, null otherwise
     * @throws IOException if it can't be saved
     */
    @Override
    public E save(E entity) throws IOException, SQLException {
        if (entity==null)
            throw new RepoException("Entity must be not null");
        validator.validate(entity);
        if(entities.get(entity.getId()) != null) {
            return entity; //throw RepoException("Entity with existing ID");
        }
        else entities.put(entity.getId(),entity);
        return null;
    }

    /**
     * Throws RepoException if the id is null
     * @param id The id that we will search for in order to delete that User
     *      id must be not null
     * @return The removed entity, null otherwise
     * @throws IOException if it can't be deleted
     */
    @Override
    public E delete(ID id) throws IOException, SQLException {
        if(entities.get(id) != null){
            return entities.remove(id);
        }
        if(id == null)
            throw new RepoException("The entity was not found!");
        return null;
    }

    /**
     * Throws RepoException if the entity is null
     * @param entity The new
     *          entity must not be null
     * @return null if it can be updated, entity otherwise
     * @throws IOException if it can't be updated
     */
    @Override
    public E update(E entity) throws IOException {

        if(entity == null)
            throw new RepoException("Entity must be not null!");
        validator.validate(entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return null;
        }
        else{
            return entity;
        }
    }
}
