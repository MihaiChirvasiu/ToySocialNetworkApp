package com.company.UI;

import com.company.Domain.Entity;
import com.company.Domain.Friendship;
import com.company.Domain.User;
import com.company.Domain.Validators.ValidationException;
import com.company.Repository.RepoException;
import com.company.Service.Controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class UI<ID, E extends Entity<ID>, E1 extends Entity<ID>> {
    private Controller<ID, E, E1> controller;

    public UI(Controller<ID, E, E1> controller) {
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
        controller.addFriendshipServ((ID) idUser, (ID) idFriend);
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
     * The menu method
     */
    public void printCommands() {
        System.out.println("Main menu");
        System.out.println("0. Print all users");
        System.out.println("1. Add a new user");
        System.out.println("2. Delete a user with the given id");
        System.out.println("3. Modify a user");
        System.out.println("4. Add a friend to a given user");
        System.out.println("5. Delete a friend of a given user");
        System.out.println("6. Find a user with the given id");
        System.out.println("7. How many communities are in the network?");
        System.out.println("8. Find the longest chain in a community");
        System.out.println("9. Find all friends");
        System.out.println("10. Find one friend");
        System.out.println("11. Update a friendship");
        System.out.println("12. Exit the application");
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
                if (command == 5) {
                    System.out.print("Give the ID of the user whose friend to be deleted ");
                    Long idUser = in.nextLong();
                    System.out.print("Give the ID of the friend to be deleted ");
                    Long idFriend = in.nextLong();
                    deleteFriendUI(idUser, idFriend);
                }
                if (command == 6) {
                    System.out.print("Give the ID of the user to be returned ");
                    Long id = in.nextLong();
                    findOneUI(id);
                }
                if(command == 7){
                    System.out.println("The number of communities is " + communitiesUI());
                }
                if(command == 8){
                    System.out.print("The longest road is ");
                    longestRoadUI();
                    System.out.println();
                }
                if (command == 9) {
                    System.out.print("Print all friendships ");
                    allFriendsUI();
                    System.out.println();
                }
                if(command == 10){
                    System.out.print("Give the ID of the user ");
                    Long id = in.nextLong();
                    System.out.print("Give the ID of the friend to be searched ");
                    Long idFriend = in.nextLong();
                    findOneFriend(id, idFriend);
                    System.out.println();
                }
                if(command == 11){
                    System.out.print("Give the ID of the first User ");
                    Long id1 = in.nextLong();
                    System.out.print("Give the ID of the second User ");
                    Long id2 = in.nextLong();
                    System.out.print("Give the ID of the new User ");
                    Long id3 = in.nextLong();
                    updateFriendUI(id1, id2, id3);
                    System.out.println();
                }
                if(command == 12){
                    in.close();
                    break;
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
