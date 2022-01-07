package com.example.toysocialnetwork.Repository.File;

import com.example.toysocialnetwork.Domain.Entity;
import com.example.toysocialnetwork.Domain.User;
import com.example.toysocialnetwork.Domain.Validators.Validator;
import com.example.toysocialnetwork.Repository.Memory.InMemoryRepositoryUser;
import com.example.toysocialnetwork.Repository.RepoException;

import java.io.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class UserFile<ID, E extends Entity<ID>> extends InMemoryRepositoryUser<ID, E> {

    String fileName;

    public UserFile(String fileName, Validator<E> validator) throws SQLException, IOException {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    /**
     * The method that loads the users from a file
     * @throws FileNotFoundException if the file given was not found
     * @throws IOException if the file given could not be opened
     */
    private void loadData() throws FileNotFoundException, IOException, SQLException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while((line = br.readLine()) != null && !line.equals("")){
            List<String> attributes = Arrays.asList(line.split(";"));
            E e = extractEntity(attributes);
            super.save(e);
        }
    }

    /**
     *
     * @param attributes The list of attributes read from the file
     * @return A user
     */
    public E extractEntity(List<String> attributes) {
        try {
            User user = new User(attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4));
            user.setId(Long.parseLong(attributes.get(0)));
            return (E) user;
        }
        catch (ArrayIndexOutOfBoundsException e){
            throw new RepoException("Not enough arguments given");
        }
    }

    /**
     *
     * @param entity The entity to be written to file
     * @return The User as String
     */
    protected String createEntityAsString(User entity) {

        return entity.getId().toString() + ";" + entity.getLastName() + ";" + entity.getFirstName();
    }

    /**
     *
     * @param entity The entity to be saved in the file
     * @return entity if it could not be saved else return null
     * @throws IOException if it could not write to file
     */
    @Override
    public E save(E entity) throws IOException, SQLException {
        if(super.save(entity) != null)
            return entity;
        writeToFile(entity);
        return null;
    }

    /**
     *
     * @param Entity the new Entity to be saved to file(updated)
     * @return null if the operation was successful else return the entity
     * @throws IOException if it could not write to file
     */
    @Override
    public E update(E Entity) throws IOException {
        if(super.update(Entity) != null)
            return Entity;
        writeEntities();
        return null;
    }

    /**
     *
     * @param id The id of the user to be deleted
     * @return null if the operation succeeded else return the entity
     * @throws IOException if it could write to file
     */
    @Override
    public E delete(ID id) throws IOException, SQLException {
        if(super.delete(id) == null){
            return findOne(id);
        }
        writeEntities();
        return null;
    }

    /**
     * The method that save the Users in file
     * @throws IOException if it could not write to file
     */
    private void writeEntities() throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false));
        for(E e : findAll()){
            bw.write(createEntityAsString((User) e));
            bw.newLine();
        }
        bw.flush();
    }

    /**
     *
     * @param entity The entity to be written to file
     * @throws IOException if it could not write to file
     */
    protected void writeToFile(E entity) throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true));
        bw.write(createEntityAsString((User) entity));
        bw.newLine();
        bw.flush();
    }

}
