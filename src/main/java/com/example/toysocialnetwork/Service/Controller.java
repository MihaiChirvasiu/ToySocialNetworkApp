package com.example.toysocialnetwork.Service;

import com.example.toysocialnetwork.Domain.*;
import com.example.toysocialnetwork.Events.ChangeEventType;
import com.example.toysocialnetwork.Events.EntityChangeEvent;
import com.example.toysocialnetwork.Observer.Observable;
import com.example.toysocialnetwork.Observer.Observer;
import com.example.toysocialnetwork.Repository.Database.DatabaseEventRepository;
import com.example.toysocialnetwork.Repository.Database.DatabaseFriendRequestRepository;
import com.example.toysocialnetwork.Repository.Database.DatabaseMessageRepository;
import com.example.toysocialnetwork.Repository.FriendshipRepository;
import com.example.toysocialnetwork.Repository.RepoException;
import com.example.toysocialnetwork.Repository.UserRepository;
import com.example.toysocialnetwork.Utils.STATUS;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Controller<ID, E extends Entity<ID>, E1 extends Entity<ID>, E2 extends Entity<ID>, E3 extends Entity<ID>, E4 extends Entity<ID>> implements Observable<EntityChangeEvent> {
    private UserRepository<ID, E> repository;
    private FriendshipRepository<ID, E1> friendshipRepository;
    private DatabaseFriendRequestRepository<ID,E2,E> friendRequestRepository;
    private DatabaseMessageRepository<ID, E3, E> messageRepository;
    private DatabaseEventRepository<ID, E4, E> eventRepository;
    private Network network;

    public Controller(UserRepository<ID, E> repository, FriendshipRepository<ID, E1> friendshipRepository, DatabaseFriendRequestRepository<ID, E2, E> friendRequestRepository
            , DatabaseMessageRepository<ID, E3, E> messageRepository, DatabaseEventRepository<ID, E4, E> eventRepository){
        this.repository = repository;
        this.friendshipRepository = friendshipRepository;
        this.friendRequestRepository=friendRequestRepository;
        this.messageRepository = messageRepository;
        this.eventRepository = eventRepository;
    }
    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for (int i = 0; i < encodedHash.length; i++) {
            String hex = Integer.toHexString(0xff & encodedHash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public void addUser(String firstName, String lastName, String email, String password) throws NoSuchAlgorithmException, IOException, SQLException{
        String hashedPassword = hashPassword(password);
        User user = new User(firstName, lastName, email, hashedPassword);
        saveServ((E) user);
    }

    public String searchEmail(String email) throws SQLException{
        return repository.findEmail(email);
    }

    public User login(String email, String password) throws SQLException, NoSuchAlgorithmException{
        return repository.loginRepo(email, hashPassword(password));
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

    /**
     *
     * @param idUser1 The id of the first user
     * @param idUser2 the id of the second user
     * @return The friendRequest if it exists, null otherwise
     * @throws SQLException Database
     */
    private E2 findFriendRequestServ(ID idUser1, ID idUser2) throws SQLException {
        FriendRequest friendRequest = new FriendRequest((User) findOneServ(idUser1),(User) findOneServ(idUser2));
        return (E2) friendRequestRepository.findFriendRequest((E2)friendRequest);
    }

    /**
     * Accept the friendRequest
     * @param idUser1 The id of the first user
     * @param idUser2 The id of the second user
     * @throws SQLException Database
     * @throws IOException File
     */
    public void acceptFriendRequest(ID idUser1, ID idUser2) throws SQLException,IOException {
        friendRequestRepository.updateStatus(findFriendRequestServ(idUser1,idUser2), STATUS.approved);
        addFriendshipServ(idUser1,idUser2);
    }

    /**
     * Rejects the friendRequest
     * @param idUser1 The id of the first user
     * @param idUser2 The id of the second user
     * @throws SQLException Database
     */
    public void rejectFriendRequest(ID idUser1, ID idUser2) throws SQLException {
        friendRequestRepository.updateStatus(findFriendRequestServ(idUser1,idUser2),STATUS.rejected);
    }

    /**
     * Adds a friendRequest in the database
     * @param idUser1 The id of the first user
     * @param idUser2 The id of the second user
     * @throws SQLException Database
     */
    public void addFriendRequest(ID idUser1, ID idUser2) throws SQLException {
        if(findFriendshipServ(idUser1,idUser2)==null) {
            FriendRequest friendRequest = new FriendRequest((User) findOneServ(idUser1), (User) findOneServ(idUser2));
            friendRequest.setDate(LocalDateTime.now());
            friendRequestRepository.addFriendRequest((E2) friendRequest);
            notifyObservers(new EntityChangeEvent(ChangeEventType.ADD, friendRequest));
        }
        else
            throw new RepoException("Already Friends!");
    }

    public void deleteFriendRequest(ID idUser1, ID idUser2) throws SQLException {
        if(findFriendshipServ(idUser1,idUser2)==null) {
            FriendRequest friendRequest = new FriendRequest((User) findOneServ(idUser1), (User) findOneServ(idUser2));
            friendRequestRepository.deleteFriendRequest((E2) friendRequest);
        }
        else
            throw new RepoException("Already Friends!");
    }

    public void addEventServ(String name, String date) throws SQLException {
        LocalDateTime actualDate = LocalDateTime.parse(date);
        PublicEvent event = new PublicEvent(name, actualDate);
        eventRepository.addEvent(event);
        notifyObservers(new EntityChangeEvent(ChangeEventType.ADD, event));
    }

    public void subscribeToEventServ(ID event, ID idUser) throws SQLException {
        if(findOneServ(idUser) != null && eventRepository.getEventByIDEvent(event) != null){
            eventRepository.subscribeToEvent((User)findOneServ(idUser), eventRepository.getEventByIDEvent(event));
            notifyObservers(new EntityChangeEvent(ChangeEventType.ADD, eventRepository.getEventByIDEvent(event)));
        }
        if(findOneServ(idUser) == null)
            throw new RepoException("User doesn't exist!");
        if(eventRepository.getEventByIDEvent(event) == null)
            throw new RepoException("Event doesn't exist!");
    }

    public void unsubscribeFromEventServ(ID event, ID idUser) throws SQLException {
        if(findOneServ(idUser) != null && eventRepository.getEventByIDEvent(event) != null){
            if(eventRepository.getUsersSubscribed(eventRepository.getEventByIDEvent(event)).contains(findOneServ(idUser))) {
                eventRepository.unsubscribeFromEvent((User) findOneServ(idUser), eventRepository.getEventByIDEvent(event));
                notifyObservers(new EntityChangeEvent(ChangeEventType.DELETE, eventRepository.getEventByIDEvent(event)));
            }
            else
                throw new RepoException("User is not subscribed to this event!");
        }
        if(findOneServ(idUser) == null)
            throw new RepoException("User doesn't exist!");
        if(eventRepository.getEventByIDEvent(event) == null)
            throw new RepoException("Event doesn't exist!");
    }

    public List<PublicEvent> getAllEvents() throws SQLException{
        return eventRepository.getPublicEvents();
    }

    public List<PublicEvent> getSubscribedEventsForUser(ID idUser) throws SQLException {
        return eventRepository.getEventByIDUser(idUser);
    }

    /**
     * Saves a message in the database
     * @param idUser1 The id of user that sends the message
     * @param idToUsers The id of the users that will receive the message
     * @param message The message sent
     * @param date The date when the message was sent
     * @throws SQLException Database
     */
    public void sendMessage(ID idUser1, List<ID> idToUsers, String message, LocalDateTime date) throws SQLException{
        Message message1 = new Message((User) findOneServ(idUser1), message, date);
        List<User> list = new ArrayList<>();
        for(int i = 0; i < idToUsers.size(); i++){
            list.add((User) findOneServ(idToUsers.get(i)));
        }
        messageRepository.addMessage((E3) message1, list);
        notifyObservers(new EntityChangeEvent(ChangeEventType.ADD, message1));
    }

    /**
     * Getter for the conversation between two users
     * @param idUser1 The id of the first user
     * @param idUser2 The id of the second user
     * @return A list of messages between the two users specified
     * @throws SQLException Database
     */
    public List<E3> getConversationServ(ID idUser1, ID idUser2) throws SQLException{
        return messageRepository.getConversation(findOneServ(idUser1), findOneServ(idUser2));
    }

    public List<E3> getAllConversationServ(ID idUser1) throws SQLException{
        return messageRepository.getAllConversation(findOneServ(idUser1));
    }

    /**
     * Reply to a message
     * @param idUser1 The id of the user that replies
     * @param idUser2 The id of the user that receives the reply
     * @param idMessage The message replied to
     * @param message The reply message
     * @param date The date when the reply happens
     * @throws SQLException Database
     */
    public void replyMessage(ID idUser1, ID idUser2, ID idMessage, String message, LocalDateTime date) throws SQLException {
        Message messageRepliedTo = (Message) messageRepository.findOneMessage(idMessage);
        Message reply = new Message((User) findOneServ(idUser1), message, date);
        List<User> list = new ArrayList<>();
        list.add((User) findOneServ(idUser2));
        messageRepository.addMessage((E3) reply, list);
        messageRepository.setReplyMessage((E3) reply, (E3) messageRepliedTo);
        notifyObservers(new EntityChangeEvent(ChangeEventType.ADD, reply));
    }

    public void replyAllMessage(ID idUser1, ID idMessage, String message, LocalDateTime date) throws SQLException {
        Message messageRepliedTo = (Message) messageRepository.findOneMessage(idMessage);
        Message reply = new Message((User) findOneServ(idUser1), message, date);
        List<User> list = new ArrayList<>();
        List<ID> idUsersTo = messageRepository.getUsersFromIDMessage((Long) idMessage);
        for(int i = 0; i < idUsersTo.size(); i++){
            if(idUsersTo.get(i) != idUser1)
                list.add((User) findOneServ(idUsersTo.get(i)));
        }
        messageRepository.addMessage((E3) reply, list);
        messageRepository.setReplyMessage((E3) reply, (E3) messageRepliedTo);
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

    /**
     * Finds all friendRequests for a user
     * @param idUser The id of the user to search the friendRequests for
     * @return A list consisting of all the friendRequests
     * @throws SQLException Database
     */
    public List<E2> findFriendRequestsForUser(ID idUser) throws SQLException {
        User user = (User) findOneServ(idUser);
        return friendRequestRepository.findAllFriendRequestsForUser(user);

    }

    public List<E2> findFriendRequestsForUserReceived(ID idUser) throws SQLException {
        User user = (User) findOneServ(idUser);
        return friendRequestRepository.findAllFriendRequestsForUserReceived(user);

    }

    /**
     * Finds all the friendships of the given user
     * @param idUser The id of the user to search the friendships for
     * @return A list consisting of all the friendships
     * @throws SQLException Database
     */
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
    private List<Observer<EntityChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<EntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<EntityChangeEvent> e) {

    }

    @Override
    public void notifyObservers(EntityChangeEvent t) {
        observers.stream().forEach(x -> {
            try {
                x.update(t);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });
    }
}
