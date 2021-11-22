package com.company.UI;

import com.company.Domain.*;
import com.company.Domain.Validators.ValidationException;
import com.company.Repository.RepoException;
import com.company.Service.Controller;
import com.company.Utils.Constants;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class UI<ID, E extends Entity<ID>, E1 extends Entity<ID>, E2 extends Entity<ID>, E3 extends Entity<ID>> {
    private Controller<ID, E, E1, E2, E3> controller;

    public UI(Controller<ID, E, E1, E2, E3> controller) {
        this.controller = controller;
    }

    /**
     *
     * @param id The id that the User will have
     * @param firstName The firstName that the User will have
     * @param lastName The lastName that the User will have
     * @throws IOException if the operation can't be executed(file)
     */
    public void addUserUI(Long id, String firstName, String lastName) throws IOException, SQLException {
        User user = new User(firstName, lastName);
        user.setId(id);
        controller.saveServ((E) user);
    }

    /**
     *
     * @param id The id of the User to be deleted
     * @throws IOException if the operation can't be executed(file)
     */
    public void deleteUserUI(Long id) throws IOException, SQLException {
        controller.deleteServ((ID) id);
    }

    /**
     *
     * @param id The id of the User to be modified
     * @param newFirstName The new firstName of that User
     * @param newLastName The new lastName of that User
     * @throws IOException if the operation can't be executed(file)
     */
    public void modifyUserUI(Long id, String newFirstName, String newLastName) throws IOException,SQLException {
        User user = new User(newFirstName, newLastName);
        user.setId(id);
        controller.updateServ((E) user);
    }

    /**
     *
     * @param id The id to search for and find the corresponding User
     */
    public void findOneUI(Long id) throws SQLException{
        User user = (User) controller.findOneServ((ID) id);
        System.out.println("ID " + user.getId() + " " + "FirstName " + user.getFirstName() + " " + "LastName " + user.getLastName());
    }

    /**
     *
     * @return The number of communities in the network
     */
    public long communitiesUI() throws SQLException{
        return controller.findTheNumberOfCommunities();
    }

    /**
     * UI Method for printing the longest road in a community
     */
    public void longestRoadUI() throws SQLException{
        Long[] road = controller.findLongestRoadInACommunity();
        int index = 0;
        while(index < road.length && road[index] != null){
            System.out.print(road[index] + " ");
            index++;
        }
    }

    /**
     *
     * @param idUser The id of the user to add a friend to
     * @param idFriend The id of the user to become a friend
     */
    public void addFriendUI(Long idUser, Long idFriend) throws IOException,SQLException {
        controller.addFriendRequest((ID) idUser, (ID) idFriend);
    }

    /**
     * Accept the friendRequest
     * @param idUser The id of the user that accepts the friendRequest
     * @param idFriend The id of the user that sent the friendRequest
     * @throws IOException File
     * @throws SQLException Database
     */
    public void acceptFriendRequest(Long idUser, Long idFriend) throws IOException,SQLException {
        controller.acceptFriendRequest((ID)idUser,(ID)idFriend);
    }

    /**
     * Reject the friendRequest
     * @param idUser The id of the user that reject the friendRequest
     * @param idFriend The id of the user that will get rejected
     * @throws SQLException Database
     */
    public void rejectFriendRequest(Long idUser, Long idFriend) throws SQLException {
        controller.rejectFriendRequest((ID)idUser,(ID)idFriend);
    }

    /**
     *
     * @param idUser The id of the user
     * @param idFriend The id of the user to be deleted from the list of friends of the first User
     */
    public void deleteFriendUI(Long idUser, Long idFriend) throws IOException,SQLException {
        controller.deleteFriendshipServ((ID) idUser, (ID) idFriend);
    }

    /**
     * Prints all the friendships
     * @throws SQLException Database
     */
    public void allFriendsUI() throws SQLException {
        List<E1> friendsList = controller.findFriendships();
        for(E1 friendship : friendsList){
            Friendship fr = (Friendship) friendship;
            System.out.println(fr.getFirstUser().getFirstName() + " " + fr.getFirstUser().getLastName() +
                    " - " + fr.getSecondUser().getFirstName() + " " + fr.getSecondUser().getLastName());
        }
    }

    /**
     * Prints all friendRequests for a given user
     * @param idUser The id of the user we will print the friendRequests for
     * @throws SQLException Database
     */
    public void allFriendRequestsUI(Long idUser) throws SQLException {
        List<E2> friendRequestsList = controller.findFriendRequestsForUser((ID)idUser);
        for(E2 friendRequest : friendRequestsList){
            FriendRequest fr = (FriendRequest) friendRequest;
            User user = (User) controller.findOneServ((ID)fr.getUser2().getId());
            System.out.println(fr.getUser1().getId() + " " + fr.getUser1().getFirstName() + " " + fr.getUser1().getLastName() +
                    " - " + user.getId() + " " + user.getFirstName() + " " + user.getLastName());
        }
    }

    /**
     *
     * @param idUser The id of the first user
     * @param oldIdUser The id of the second user that will be updated
     * @param newIdUser The id of the user that will replace the old user
     * @throws SQLException Database
     * @throws IOException File
     */
    public void updateFriendUI(Long idUser, Long oldIdUser, Long newIdUser) throws SQLException, IOException {
        controller.updateFriendshipServ((ID) idUser, (ID) oldIdUser, (ID) newIdUser);
    }

    /**
     *
     * @param idUser The id of the first user
     * @param idFriend The id of the second user
     * @throws SQLException Database
     */
    public void findOneFriend(Long idUser, Long idFriend) throws SQLException {
        Friendship friendship = (Friendship) controller.findFriendshipServ((ID) idUser, (ID) idFriend);
        System.out.println(friendship.getFirstUser().getFirstName() + " " + friendship.getFirstUser().getLastName() +
                " - " + friendship.getSecondUser().getFirstName() + " " + friendship.getSecondUser().getLastName());
    }

    /**
     * Prints all the existing users
     * @throws SQLException Database
     */
    public void printAll() throws SQLException {
        Set<ID> keys = controller.getKeysServ();
        for(ID k : keys){
            User user = (User) controller.findOneServ(k);
            System.out.println(user.getId() + " " + user.getFirstName() + " " + user.getLastName());
        }
    }

    /**
     * Prints all friends for a given user
     * @param idUser The id of the user we will print friends for
     * @throws SQLException Database
     */
    public void printAllFriendsForUser(Long idUser) throws SQLException {
        controller.findFriendshipsForUser((ID) idUser).stream().forEach(
                x-> System.out.println(((Friendship) x).getSecondUser().getLastName() + " | " +
                        ((Friendship) x).getSecondUser().getFirstName() + " | " +
                        ((Friendship) x).getDate().format(Constants.DATE_TIME_FORMATTER)));
    }

    /**
     * Prints all friends from a given month
     * @param idUser The id of the user we will print friends for
     * @param month The month specified
     * @throws SQLException Database
     */
    public void printUsersFriendshipsFromMonth(Long idUser,int month) throws SQLException {
        controller.findFriendshipsForUser((ID)idUser).stream()
                .filter(
                        x -> ((Friendship) x).getDate().getMonthValue()==month
                )
                .forEach(x-> System.out.println(((Friendship) x).getSecondUser().getLastName() + " | " +
                        ((Friendship) x).getSecondUser().getFirstName() + " | " +
                        ((Friendship) x).getDate().format(Constants.DATE_TIME_FORMATTER)));
    }

    /**
     * Sends a message
     * @param idUser The id of the user that sens the message
     * @param toIDUsers The list of ids of the users that will receive the message
     * @param message The message that will be sent
     * @throws SQLException Database
     */
    public void sendMessageUI(Long idUser, List<Long> toIDUsers, String message) throws SQLException {
        controller.sendMessage((ID) idUser, (List<ID>) toIDUsers, message, LocalDateTime.now());
    }

    /**
     * Prints the conversation between two users
     * @param idUser1 The id of the first user
     * @param idUser2 The id of the second user
     * @throws SQLException Database
     */
    public void printConversation(Long idUser1, Long idUser2) throws SQLException{
        List<Message> messageList = (List<Message>) controller.getConversationServ((ID) idUser1, (ID) idUser2);
        for(int i = 0; i < messageList.size(); i++){
            if(messageList.get(i).getReplyMessage() == null)
                System.out.println(messageList.get(i).getMessage() + " " + messageList.get(i).getDate().format(Constants.DATE_TIME_FORMATTER));
            else
                System.out.println("    " + messageList.get(i).getMessage() + " " + messageList.get(i).getDate().format(Constants.DATE_TIME_FORMATTER));
        }
    }

    /**
     * Prints the conversation indexed between two users
     * @param idUser1 The id of the first user
     * @param idUser2 The id of the second user
     * @throws SQLException Database
     */
    public void printIndexedConversation(Long idUser1, Long idUser2) throws SQLException{
        List<Message> messageList = (List<Message>) controller.getConversationServ((ID) idUser1, (ID) idUser2);
        for(int i = 0; i < messageList.size(); i++){
            if(messageList.get(i).getReplyMessage() == null)
                System.out.println(i + " " + messageList.get(i).getMessage() + " " + messageList.get(i).getDate().format(Constants.DATE_TIME_FORMATTER));
            else
                System.out.println(i + " " + "    " + messageList.get(i).getMessage() + " " + messageList.get(i).getDate().format(Constants.DATE_TIME_FORMATTER));
        }
    }

    /**
     * Sends a reply message
     * @param idUser1 The id of the user that sends the reply
     * @param idUser2 The id of the user that will receive the reply
     * @param index The index to reply to
     * @param message The reply message
     * @throws SQLException Database
     */
    public void replyMessageUI(Long idUser1, Long idUser2, int index, String message) throws SQLException{
        controller.replyMessage((ID) idUser1, (ID) idUser2, index, message, LocalDateTime.now());
    }

    /**
     * The menu method
     */
    public void printCommands() {
        System.out.println("Main menu");
        System.out.println("0. Print all users");
        System.out.println("1. Add a new user");
        System.out.println("2. Delete a user with the given id");
        System.out.println("3. Modify a user");
        System.out.println("4. Send a friend request");
        System.out.println("5. Accept a friend request");
        System.out.println("6. Reject a friend request");
        System.out.println("7. Delete a friend of a given user");
        System.out.println("8. Find a user with the given id");
        System.out.println("9. How many communities are in the network?");
        System.out.println("10. Find the longest chain in a community");
        System.out.println("11. Find all friends");
        System.out.println("12. Find one friend");
        System.out.println("13. Update a friendship");
        System.out.println("14. Show all friends for an user");
        System.out.println("15. Show all users who are friends with the given user from the specified month");
        System.out.println("16. Send a message");
        System.out.println("17. Reply to a message");
        System.out.println("18. Show conversation");
        System.out.println("19. Exit the application");
        System.out.print("Give the desired command ");
    }

    /**
     * The method that will represent the menu and the different branches for the functionalities
     * Prints the corresponding error messages in case of an Exception
     */
    public void run() {
        Scanner in = new Scanner(System.in);
        while (true) {
            printCommands();
            //try pentru comanda invalida si pentru input uri invalide
            try {
                int command = in.nextInt();
                if(command == 0){
                    printAll();
                }
                if (command == 1) {
                    System.out.print("Give the ID ");
                    Long id = in.nextLong();
                    System.out.print("Give the firstName ");
                    String firstName = in.next();
                    System.out.print("Give the lastName ");
                    String lastName = in.next();
                    addUserUI(id, firstName, lastName);
                }
                if (command == 2) {
                    System.out.print("Give the ID of the user to be deleted ");
                    Long id = in.nextLong();
                    deleteUserUI(id);
                }
                if (command == 3) {
                    System.out.print("Give the ID of the user to be modified ");
                    Long id = in.nextLong();
                    System.out.print("Give the new FirstName of the user ");
                    String newFirstName = in.next();
                    System.out.print("Give the new LastName of the user ");
                    String newLastName = in.next();
                    modifyUserUI(id, newFirstName, newLastName);
                }
                if (command == 4) {
                    System.out.print("Give the ID of the user ");
                    Long idUser = in.nextLong();
                    System.out.print("Give the ID of the user to become a friend ");
                    Long idFriend = in.nextLong();
                    addFriendUI(idUser, idFriend);
                }
                if(command == 5)
                {
                    System.out.print("Give the ID of the user ");
                    Long idUser = in.nextLong();
                    allFriendRequestsUI(idUser);
                    if(!controller.findFriendRequestsForUser((ID)idUser).isEmpty()) {
                        System.out.println("Give the friend whose request you want to accept");
                        Long idFriend = in.nextLong();
                        acceptFriendRequest(idUser, idFriend);
                    }
                    else
                        System.out.println("There are no pending friend requests");
                }
                if(command == 6)
                {
                    System.out.print("Give the ID of the user ");
                    Long idUser = in.nextLong();
                    allFriendRequestsUI(idUser);
                    if(!controller.findFriendRequestsForUser((ID)idUser).isEmpty()) {
                        System.out.println("Give the friend whose request you want to reject");
                        Long idFriend = in.nextLong();
                        rejectFriendRequest(idUser,idFriend);
                    }
                    else
                        System.out.println("There are no pending friend requests");
                }
                if (command == 7) {
                    System.out.print("Give the ID of the user whose friend to be deleted ");
                    Long idUser = in.nextLong();
                    System.out.print("Give the ID of the friend to be deleted ");
                    Long idFriend = in.nextLong();
                    deleteFriendUI(idUser, idFriend);
                }
                if (command == 8) {
                    System.out.print("Give the ID of the user to be returned ");
                    Long id = in.nextLong();
                    findOneUI(id);
                }
                if(command == 9){
                    System.out.println("The number of communities is " + communitiesUI());
                }
                if(command == 10){
                    System.out.print("The longest road is ");
                    longestRoadUI();
                    System.out.println();
                }
                if (command == 11) {
                    System.out.print("Print all friendships ");
                    allFriendsUI();
                    System.out.println();
                }
                if(command == 12){
                    System.out.print("Give the ID of the user ");
                    Long id = in.nextLong();
                    System.out.print("Give the ID of the friend to be searched ");
                    Long idFriend = in.nextLong();
                    findOneFriend(id, idFriend);
                    System.out.println();
                }
                if(command == 13){
                    System.out.print("Give the ID of the first User ");
                    Long id1 = in.nextLong();
                    System.out.print("Give the ID of the second User ");
                    Long id2 = in.nextLong();
                    System.out.print("Give the ID of the new User ");
                    Long id3 = in.nextLong();
                    updateFriendUI(id1, id2, id3);
                    System.out.println();
                }
                if(command == 19){
                    in.close();
                    break;
                }
                if(command == 14){
                    System.out.print("Give the ID of User whose friends you want to show ");
                    Long idUser = in.nextLong();
                    printAllFriendsForUser(idUser);
                }
                if(command == 15)
                {
                    System.out.println("Give the ID of User whose friends you want to show ");
                    Long idUser = in.nextLong();
                    System.out.println("Give the month they started being friends ");
                    Integer month = in.nextInt();
                    printUsersFriendshipsFromMonth(idUser,month);
                }
                if(command == 16){
                    List<Long> idUsersTo = new ArrayList<>();
                    System.out.println("Give the ID of the user who sends a message ");
                    Long idUser = in.nextLong();
                    System.out.println("Give the message you want to send ");
                    String message = in.next();
                    message += in.nextLine();
                    while(true){
                        System.out.println("Give the ID of the user who receives the message ");
                        Long idUserTo = in.nextLong();
                        idUsersTo.add(idUserTo);
                        System.out.println("Do you want to send the message to anyone else? [y/n]");
                        if(Objects.equals(in.next().toLowerCase(), "n"))
                            break;
                    }
                    sendMessageUI(idUser, idUsersTo, message);
                }
                if(command == 18){
                    System.out.println("Give the ID of the first user whose conversation you want to show ");
                    Long idUser1 = in.nextLong();
                    System.out.println("Give the ID of the second user whose conversation you want to show ");
                    Long idUser2 = in.nextLong();
                    printConversation(idUser1, idUser2);
                }
                if(command == 17){
                    System.out.println("Give the ID of the User who replies to a message ");
                    Long idUser1 = in.nextLong();
                    System.out.println("Give the ID of the User with whom the other user converses");
                    Long idUser2 = in.nextLong();
                    printIndexedConversation(idUser1, idUser2);
                    System.out.println();
                    System.out.println("Give the ID of the message the user is replying to ");
                    Integer index = in.nextInt();
                    System.out.println("Write the message ");
                    String message = in.next();
                    message += in.nextLine();
                    replyMessageUI(idUser1, idUser2, index, message);
                }
            }
            catch (InputMismatchException e){
                System.out.println("Invalid command");
                in.next();
            }
            catch (ValidationException e){
                System.out.println(e.getMessage());
            }
            catch (RepoException e){
                System.out.println(e.getErrorMessage());
            }
            catch (IllegalArgumentException e){
                System.out.println(e.getMessage());
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
            catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }
    }
}
