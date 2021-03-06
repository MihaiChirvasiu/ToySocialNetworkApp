package com.example.toysocialnetwork.Service;

import com.example.toysocialnetwork.Domain.*;
import com.example.toysocialnetwork.Events.ChangeEventType;
import com.example.toysocialnetwork.Events.EntityChangeEvent;
import com.example.toysocialnetwork.Observer.Observable;
import com.example.toysocialnetwork.Observer.Observer;
import com.example.toysocialnetwork.Paging.Page;
import com.example.toysocialnetwork.Paging.Pageable;
import com.example.toysocialnetwork.Paging.PageableImplementation;
import com.example.toysocialnetwork.Repository.Database.DatabaseEventRepository;
import com.example.toysocialnetwork.Repository.Database.DatabaseFriendRequestRepository;
import com.example.toysocialnetwork.Repository.Database.DatabaseGroupChatRepository;
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
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Controller<ID, E extends Entity<ID>, E1 extends Entity<ID>, E2 extends Entity<ID>, E3 extends Entity<ID>, E4 extends Entity<ID>, E5 extends Entity<ID>> implements Observable<EntityChangeEvent> {
    private UserRepository<ID, E> repository;
    private FriendshipRepository<ID, E1> friendshipRepository;
    private DatabaseFriendRequestRepository<ID,E2,E> friendRequestRepository;
    private DatabaseMessageRepository<ID, E3, E, E5> messageRepository;
    private DatabaseEventRepository<ID, E4, E> eventRepository;
    private DatabaseGroupChatRepository<ID, E5, E> groupChatRepository;
    private Network network;

    public Controller(UserRepository<ID, E> repository, FriendshipRepository<ID, E1> friendshipRepository, DatabaseFriendRequestRepository<ID, E2, E> friendRequestRepository
            , DatabaseMessageRepository<ID, E3, E, E5> messageRepository, DatabaseEventRepository<ID, E4, E> eventRepository, DatabaseGroupChatRepository<ID, E5, E> groupChatRepository){
        this.repository = repository;
        this.friendshipRepository = friendshipRepository;
        this.friendRequestRepository=friendRequestRepository;
        this.messageRepository = messageRepository;
        this.eventRepository = eventRepository;
        this.groupChatRepository = groupChatRepository;
    }

    /**
     * Hashing the password
     * @param password the password to be hashed
     * @return the hashed password
     * @throws NoSuchAlgorithmException hash
     */
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

    /**
     * Adds the user to the database
     * @param firstName of the user
     * @param lastName of the user
     * @param email of the user
     * @param password of the user
     * @throws NoSuchAlgorithmException hash
     * @throws IOException file
     * @throws SQLException database
     */
    public void addUser(String firstName, String lastName, String email, String password) throws NoSuchAlgorithmException, IOException, SQLException{
        String hashedPassword = hashPassword(password);
        User user = new User(firstName, lastName, email, hashedPassword);
        saveServ((E) user);
    }

    /**
     *
     * @param email the email to be searched
     * @return the email
     * @throws SQLException database
     */
    public String searchEmail(String email) throws SQLException{
        return repository.findEmail(email);
    }

    /**
     *
     * @param email the email to be verified
     * @param password the password to be verified
     * @return a user with the given credentials
     * @throws SQLException database
     * @throws NoSuchAlgorithmException hash
     */
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

    /**
     * Deletes a friendRequest
     * @param idUser1 the first user
     * @param idUser2 the second user
     * @throws SQLException database
     */
    public void deleteFriendRequest(ID idUser1, ID idUser2) throws SQLException {
        if(findFriendshipServ(idUser1,idUser2)==null) {
            FriendRequest friendRequest = new FriendRequest((User) findOneServ(idUser1), (User) findOneServ(idUser2));
            friendRequestRepository.deleteFriendRequest((E2) friendRequest);
        }
        else
            throw new RepoException("Already Friends!");
    }

    /**
     * Adds an event to the database
     * @param name the name of the event
     * @param date the date of the event
     * @throws SQLException database
     */
    public void addEventServ(String name, String date) throws SQLException {
        LocalDateTime actualDate = LocalDateTime.parse(date);
        PublicEvent event = new PublicEvent(name, actualDate);
        eventRepository.addEvent(event);
        notifyObservers(new EntityChangeEvent(ChangeEventType.ADD, event));
    }

    /**
     *
     * @param event the ID of the event
     * @param idUser the ID of the user that will subscribe to the event
     * @throws SQLException database
     */
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

    /**
     *
     * @param event the ID of the event
     * @param idUser the ID of the user that will unsubscribe from the event
     * @throws SQLException database
     */
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

    /**
     *
     * @return a list of all events from the database
     * @throws SQLException database
     */
    public List<PublicEvent> getAllEvents() throws SQLException{
        return eventRepository.getPublicEvents();
    }

    /**
     *
     * @param idUser the id of the user
     * @return a list of public events the user is subscribed to
     * @throws SQLException database
     */
    public List<PublicEvent> getSubscribedEventsForUser(ID idUser) throws SQLException {
        return eventRepository.getEventByIDUser(idUser);
    }

    /**
     *
     * @param idUser the id of the User
     * @return a list of ordered events the user is subscribed to
     * @throws SQLException database
     */
    public List<PublicEvent> getSubscribedEventsForUserOrdered(ID idUser) throws SQLException {
        return eventRepository.getEventByIDUserOrderByDate(idUser,LocalDateTime.now());
    }

    /**
     *
     * @param name of the group to be added
     * @param idUser the user that creates the group
     * @throws SQLException database
     */
    public void addGroup(String name, ID idUser) throws SQLException {
        GroupChat groupChat = new GroupChat(name);
        groupChatRepository.addGroup(groupChat);
        joinGroupServ((ID) groupChat.getId(), idUser, groupChat.getJoinCode());
    }

    /**
     *
     * @param joinCode the joinCode that will be searched
     * @return the group with the given joinCode
     * @throws SQLException database
     */
    public GroupChat getGroupByJoinCode(String joinCode) throws SQLException {
        return groupChatRepository.getGroupByJoinCode(joinCode);
    }

    /**
     *
     * @param idGroup the id of the group
     * @param idUser the id of the user that wants to join a group
     * @param joinCode the joinCode
     * @throws SQLException database
     */
    public void joinGroupServ(ID idGroup, ID idUser, String joinCode) throws SQLException {
        if(getGroupByJoinCode(joinCode) != null){
            groupChatRepository.joinGroup((User) findOneServ(idUser), groupChatRepository.getGroupByIDGroup(idGroup));
            notifyObservers(new EntityChangeEvent(ChangeEventType.ADD, groupChatRepository.getGroupByIDGroup(idGroup)));
        }
    }

    /**
     *
     * @param idGroup the group the user will leave
     * @param idUser the id of the user that will leave
     * @throws SQLException database
     */
    public void leaveGroupServ(ID idGroup, ID idUser) throws SQLException {
        if(findOneServ(idUser) != null && groupChatRepository.getGroupByIDGroup(idGroup) != null){
            if(groupChatRepository.getUsersFromGroup(groupChatRepository.getGroupByIDGroup(idGroup)).contains(findOneServ(idUser))) {
                groupChatRepository.leaveGroup((User) findOneServ(idUser), groupChatRepository.getGroupByIDGroup(idGroup));
                notifyObservers(new EntityChangeEvent(ChangeEventType.DELETE, groupChatRepository.getGroupByIDGroup(idGroup)));
            }
            else
                throw new RepoException("User is not in this group!");
        }
        if(findOneServ(idUser) == null)
            throw new RepoException("User doesn't exist!");
        if(groupChatRepository.getGroupByIDGroup(idGroup) == null)
            throw new RepoException("Group doesn't exist!");
    }

    /**
     *
     * @param idUser the id of the user
     * @return a list of all groups the user is in
     * @throws SQLException database
     */
    public List<GroupChat> getGroupChatByIDUser(ID idUser) throws SQLException {
        return groupChatRepository.getGroupChatByIDUser(idUser);
    }

    /**
     *
     * @param idGroup of the group
     * @return a list of all users from that group
     * @throws SQLException database
     */
    public List<User> getUsersFromGroupServ(ID idGroup) throws SQLException {
        return groupChatRepository.getUsersFromGroup(groupChatRepository.getGroupByIDGroup(idGroup));
    }

    /**
     * Saves a message in the database
     * @param idUser1 The id of user that sends the message
     * @param idToUsers The id of the users that will receive the message
     * @param message The message sent
     * @param date The date when the message was sent
     * @throws SQLException Database
     */
    public void sendMessage(ID idUser1, List<ID> idToUsers, String message, LocalDateTime date, ID idGroup) throws SQLException{
        Message message1 = new Message((User) findOneServ(idUser1), message, date);
        if(!idGroup.equals(-1))
            messageRepository.setMessageGroup((E3) message1, (E5) groupChatRepository.getGroupByIDGroup(idGroup));
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

    /**
     *
     * @param idUser1 of a user
     * @return a list of all messages sent
     * @throws SQLException database
     */
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

    /**
     *
     * @param idUser1 the id of the user that replies
     * @param idMessage the message replied to
     * @param message the reply message
     * @param date the data of the reply
     * @throws SQLException database
     */
    public void replyAllMessage(ID idUser1, ID idMessage, String message, LocalDateTime date) throws SQLException {
        Message messageRepliedTo = (Message) messageRepository.findOneMessage(idMessage);
        Message reply = new Message((User) findOneServ(idUser1), message, date);
        List<User> list = new ArrayList<>();
        List<ID> idUsersTo = messageRepository.getUsersFromIDMessage((Long) idMessage);
        for(int i = 0; i < idUsersTo.size(); i++){
            if(idUsersTo.get(i) != idUser1)
                list.add((User) findOneServ(idUsersTo.get(i)));
        }
        messageRepository.setMessageGroup((E3) reply, (E5) messageRepliedTo.getGroupChat());
        messageRepository.addMessage((E3) reply, list);
        messageRepository.setReplyMessage((E3) reply, (E3) messageRepliedTo);
    }

    /**
     *
     * @param idUser1 the id of the user
     * @param idGroup the id of the group
     * @return a list of all messages sent by the user in the group
     * @throws SQLException database
     */
    public List<E3> getConversationServAll(ID idUser1, ID idGroup) throws SQLException{
        return messageRepository.getConversationGroup(findOneServ(idUser1), (E5) groupChatRepository.getGroupByIDGroup(idGroup));
    }

    /**
     * Gets all messages between the two users from the given period
     * @param idUser1 the first user
     * @param idUser2 the second user
     * @param start the start date
     * @param end the end date
     * @throws SQLException database
     * @throws IOException file
     */
    public void queryMessages(ID idUser1, ID idUser2, LocalDate start, LocalDate end) throws SQLException, IOException {
        List<Message> messageList = (List<Message>) messageRepository.getConversation(findOneServ(idUser1), findOneServ(idUser2));
        String startString = start.toString() + "T00:00:00.0";
        String endString = end.toString() + "T23:59:59.9";
        LocalDateTime startDate = LocalDateTime.parse(startString);
        LocalDateTime endDate = LocalDateTime.parse(endString);
        List<Message> messagesFromPeriod = new ArrayList<>();
        for(int i = 0; i < messageList.size(); i++){
            if(!messageList.get(i).getFromUser().getId().equals(idUser1))
                if(messageList.get(i).getDate().isAfter(startDate) && messageList.get(i).getDate().isBefore(endDate))
                    messagesFromPeriod.add(messageList.get(i));
        }
        PDFReport report = new PDFReport("QueryMessages");
        report.writeToFile("Messages", messagesFromPeriod);
    }

    /**
     *
     * @param idUser1 the first user
     * @param idUser2 the second user
     * @return the first message sent between the two users
     * @throws SQLException database
     */
    public LocalDate getFirstMessage(ID idUser1, ID idUser2) throws SQLException {
        List<Message> messageList = (List<Message>) messageRepository.getConversation(findOneServ(idUser1), findOneServ(idUser2));
        if(messageList != null)
        {
            LocalDate start = messageList.get(0).getDate().toLocalDate();
            for (int i = 1; i < messageList.size(); i++) {
                if (messageList.get(i).getDate().toLocalDate().isBefore(ChronoLocalDate.from(start)) || messageList.get(i).getDate().toLocalDate().equals(ChronoLocalDate.from(start)))
                    start = messageList.get(i).getDate().toLocalDate();
            }
            return start;
        }
        throw new RepoException("You have not exchanged messages with this person yet!");
    }

    /**
     *
     * @param idUser1 the first user
     * @param idUser2 the second user
     * @return the last message sent between the two users
     * @throws SQLException database
     */
    public LocalDate getLastMessage(ID idUser1, ID idUser2) throws SQLException {
        List<Message> messageList = (List<Message>) messageRepository.getConversation(findOneServ(idUser1), findOneServ(idUser2));
        LocalDate end = messageList.get(0).getDate().toLocalDate();
        for(int i = 1; i < messageList.size(); i++){
            if(messageList.get(i).getDate().toLocalDate().isAfter(ChronoLocalDate.from(end)) || messageList.get(i).getDate().toLocalDate().equals(ChronoLocalDate.from(end)))
                end = messageList.get(i).getDate().toLocalDate();
        }
        return end;
    }

    /**
     *
     * @param idUser1 the user for whom th report to be created
     * @param start the start date
     * @param end the end date
     * @throws SQLException database
     * @throws IOException file
     */
    public void queryFriend(ID idUser1, LocalDate start, LocalDate end) throws SQLException, IOException {
        String startString = start.toString() + "T00:00:00.0";
        String endString = end.toString() + "T23:59:59.9";
        LocalDateTime startDate = LocalDateTime.parse(startString);
        LocalDateTime endDate = LocalDateTime.parse(endString);
        List<Friendship> friends = (List<Friendship>) friendshipRepository.findAllFriendshipsForUser(idUser1);
        List<Friendship> friendFromPeriod = new ArrayList<>();
        for(int i = 0; i < friends.size(); i++){
            if(friends.get(i).getDate().isAfter(startDate) && friends.get(i).getDate().isBefore(endDate))
            friendFromPeriod.add(friends.get(i));
        }
        List<Message> messageList = (List<Message>) messageRepository.getAllReceivedMessages(findOneServ(idUser1));
        List<Message> messages = new ArrayList<>();
        for(int i = 0; i < messageList.size(); i++){
                if(messageList.get(i).getDate().isAfter(startDate) && messageList.get(i).getDate().isBefore(endDate))
                    messages.add(messageList.get(i));
        }
        PDFReport report = new PDFReport("ActivityQuery");
        report.writeToFileFriends("Activity", messages, friendFromPeriod);
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
    private int page = 0;
    private int size = 1;
    private Pageable pageable;

    public void setPageSize(int size) {
        this.size = size;
    }

    public Set<User> getNextUsers() throws SQLException {
        this.page++;
        return getMessagesOnPage(this.page);
    }

    public Set<User> getMessagesOnPage(int page) throws SQLException {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<User> studentPage = (Page<User>) repository.findAllPage(pageable);
        return studentPage.getContent().collect(Collectors.toSet());
    }

}
