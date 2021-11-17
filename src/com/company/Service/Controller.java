package com.company.Service;

import com.company.Domain.*;
import com.company.Repository.Database.DatabaseFriendRequestRepository;
import com.company.Repository.FriendshipRepository;
import com.company.Repository.UserRepository;
import com.company.Utils.STATUS;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class Controller<ID, E extends Entity<ID>, E1 extends Entity<ID>, E2 extends Entity<ID>> {
    private UserRepository<ID, E> repository;
    private FriendshipRepository<ID, E1> friendshipRepository;
    private DatabaseFriendRequestRepository<ID,E2> friendRequestRepository;
    private Network network;

    public Controller(UserRepository<ID, E> repository, FriendshipRepository<ID, E1> friendshipRepository, DatabaseFriendRequestRepository<ID, E2> friendRequestRepository){
        this.repository = repository;
        this.friendshipRepository = friendshipRepository;
        this.friendRequestRepository=friendRequestRepository;
    }

    /**
     *
     * @param entity The User that we want to save in the repository
     * @return null if the operation is successful, entity otherwise
     * @throws IOException if it can't be saved(file)
     */
    public E saveServ(E entity) throws IOException, SQLException {
        return repository.save(entity);
    }

    /**
     *
     * @param id The id to be searched in order to delete the User with the corresponding id
     * @return null if successful, entity otherwise
     * @throws IOException if it can't be deleted(file)
     */
    public E deleteServ(ID id) throws IOException, SQLException {
        Set<ID> keys = repository.getKeys();
        for(ID key : keys){
            if(key != id) {
                Friendship friendship = (Friendship) findFriendshipServ(id, key);
                if (friendship != null) {
                    friendshipRepository.deleteFriendshipRepo((E1) friendship);
                }
                Friendship reverseFriendship = (Friendship) findFriendshipServ(key, id);
                if (reverseFriendship != null) {
                    friendshipRepository.deleteFriendshipRepo((E1) reverseFriendship);
                }
            }
        }
        return repository.delete(id);
    }

    /**
     *
     * @param id The id that we will search in the repo
     * @return the entity associated with that id, null otherwise
     */
    public E findOneServ(ID id) throws SQLException{
        return repository.findOne(id);
    }

    /**
     *
     * @return The size of the repository(how many Users)
     */
    public int getSizeServ() throws SQLException {
        return repository.getSize();
    }

    /**
     *
     * @return The set of the Ids
     */
    public Set<ID> getKeysServ() throws SQLException{
        return repository.getKeys();
    }

    /**
     *
     * @param entity The new modified entity
     * @return null if the operation is successful, entity otherwise
     * @throws IOException if the entity can't be updated(file)
     */
    public E updateServ(E entity) throws IOException,SQLException {
        E updatedEntity = repository.update(entity);
        Set<ID> keys = getKeysServ();
        for(ID key : keys){
            if(key != entity.getId()) {
                Friendship friendship = (Friendship) findFriendshipServ(entity.getId(), key);
                if (friendship != null) {
                    updateFriendshipServ(entity.getId(), key, key);
                }
                Friendship reverseFriendship = (Friendship) findFriendshipServ(key, entity.getId());
                if (reverseFriendship != null) {
                    updateFriendshipServ(key, entity.getId(), entity.getId());
                }
            }
        }
        return updatedEntity;
    }

    /**
     * The method that establishes a friendship relation between two Users
     * @param idUser1 The id of a User
     * @param idUser2 The id of another User
     */
    private void addFriendshipServ(ID idUser1, ID idUser2) throws SQLException, IOException {
        if(findOneServ(idUser1) != null && findOneServ(idUser2) != null) {
            Friendship friendship = new Friendship((User) findOneServ(idUser1), (User) findOneServ(idUser2));
            friendshipRepository.addFriendshipRepo((E1) friendship);
        }
    }

    private E2 findFriendRequestServ(ID idUser1, ID idUser2) throws SQLException
    {
        FriendRequest friendRequest = new FriendRequest((User) findOneServ(idUser1),(User) findOneServ(idUser2));
        return (E2) friendRequestRepository.findFriendRequest((E2)friendRequest);
    }

    public void acceptFriendRequest(ID idUser1, ID idUser2) throws SQLException,IOException
    {
        friendRequestRepository.updateStatus(findFriendRequestServ(idUser1,idUser2), STATUS.approved);
        addFriendshipServ(idUser1,idUser2);
    }

    public void rejectFriendRequest(ID idUser1, ID idUser2) throws SQLException
    {
        friendRequestRepository.updateStatus(findFriendRequestServ(idUser1,idUser2),STATUS.rejected);
    }

    public void addFriendRequest(ID idUser1, ID idUser2) throws SQLException
    {
        if(findFriendshipServ(idUser1,idUser2)==null) {
            FriendRequest friendRequest = new FriendRequest((User) findOneServ(idUser1), (User) findOneServ(idUser2));
            friendRequestRepository.addFriendRequest((E2) friendRequest);
        }
    }

    /**
     * Method that deletes a friend from a user
     * @param idUser1 The id of a User
     * @param idUser2 The id of another user
     */
    public void deleteFriendshipServ(ID idUser1, ID idUser2) throws SQLException, IOException {
        if(findOneServ(idUser1) != null && findOneServ(idUser2) != null) {
            Friendship friendship = new Friendship((User) findOneServ(idUser1), (User) findOneServ(idUser2));
            friendshipRepository.deleteFriendshipRepo((E1) friendship);
        }
    }

    /**
     *
     * @param idUser1 The id of the first user
     * @param idUser2 The id of the second user
     * @return The friendship between the two users or null if it doesn't exist
     * @throws SQLException Inheritance(Database)
     */
    public E1 findFriendshipServ(ID idUser1, ID idUser2) throws SQLException {
        if(findOneServ(idUser1) != null && findOneServ(idUser2) != null) {
            Friendship friendship = new Friendship((User) findOneServ(idUser1), (User) findOneServ(idUser2));
            return friendshipRepository.findOneFriendship((E1) friendship);
        }
       return null;
    }

    /**
     *
     * @return A list consisting of all the friendships found in the repository
     * @throws SQLException Inheritance(Database)
     */
    public List<E1> findFriendships() throws SQLException {
        return friendshipRepository.findAllFriendships();
    }

    public List<E2> findFriendRequestsForUser(ID idUser) throws SQLException {
        User user = (User) findOneServ(idUser);
        return friendRequestRepository.findAllFriendRequestsForUser(user);

    }
    public List<E1> findFriendshipsForUser(ID idUser) throws SQLException {
        return friendshipRepository.findAllFriendshipsForUser(idUser);
    }

    /**
     *
     * @param idUser1 The id of the first user from a friendship
     * @param idUser2 The id of the second user from a friendship
     * @param idUser3 The id of the third user that will form a new friendship with the first user
     * @throws SQLException Inheritance(Database)
     * @throws IOException Inheritance(file)
     */
    public void updateFriendshipServ(ID idUser1, ID idUser2, ID idUser3) throws SQLException, IOException {
       if(findOneServ(idUser1) != null && findOneServ(idUser2) != null && findOneServ(idUser3) != null){
           User friend1 = (User) findOneServ(idUser1);
           User friend2 = (User) findOneServ(idUser2);
           User friend3 = (User) findOneServ(idUser3);
           Friendship oldFriendship = new Friendship(friend1, friend2);
           Friendship newFriendship = new Friendship(friend1, friend3);
           friendshipRepository.updateFriendship((E1) oldFriendship, (E1) newFriendship);
       }
    }

    /**
     *
     * @return The maximum id from the repository
     */
    public Long getMaxx() throws SQLException{
        long maxx = 0;
        Set<ID> keys= getKeysServ();
        for(ID k : keys){
            if((Long) k > maxx){
                maxx = (long) k;
            }
        }
        return maxx;
    }

    /**
     *
     * @return The number of connected communities in the network graph
     */
    public long findTheNumberOfCommunities() throws SQLException{
        long maxx = 0;
        Set<ID> keys= getKeysServ();
        for(ID k : keys){
            if((Long) k > maxx){
                maxx = (long) k;
            }
        }
        this.network = new Network(Math.toIntExact(getMaxx()), getKeysServ(), this.repository, this.friendshipRepository);
        return network.numberOfCommunities(getKeysServ(), (int) maxx);
    }

    /**
     *
     * @return The array that will contain the longest road from a connected community
     */
    public Long[] findLongestRoadInACommunity() throws SQLException{

        long maxx = 0;
        Set<ID> keys= getKeysServ();
        for(ID k : keys){
            if((Long) k > maxx){
                maxx = (long) k;
            }
        }
        this.network = new Network(Math.toIntExact(getMaxx()), getKeysServ(), this.repository, this.friendshipRepository);
        return network.longestRoadInACommunity(getKeysServ(), (int) maxx);
    }

}
