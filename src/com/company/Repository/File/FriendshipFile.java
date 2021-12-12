package com.company.Repository.File;

import com.company.Domain.Entity;
import com.company.Domain.Friendship;
import com.company.Domain.User;
import com.company.Domain.Validators.Validator;
import com.company.Repository.Memory.InMemoryRepositoryFriendship;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class FriendshipFile <ID, E extends Entity<ID>> extends InMemoryRepositoryFriendship<ID, E> {

    String fileName;
    Validator<E> validator;

    public FriendshipFile(String fileName, Validator<E> validator) throws IOException {
        super(validator);
        this.fileName = fileName;
        loadFriendships();
    }

    /**
     * Loads the friendships between users from a file
     * @throws FileNotFoundException if the file given was not found
     * @throws IOException if the file given could not be opened
     */
    private void loadFriendships() throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while((line = br.readLine()) != null && !line.equals("")){
            List<String> attributes = Arrays.asList(line.split(";"));
            addFriendshipsFromFile(attributes);
        }
    }

    /**
     * Writes the friendships between users to file
     * @throws IOException if the file could not be open
     */
    private void writeFriendships() throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false));
        for(E e : findAllFriendships()){
            bw.write(createFriendshipsAsString(e));
            bw.newLine();
        }
        bw.flush();
    }

    /**
     *
     * @return The friendship as a String else null if it could not be converted
     */
    protected String createFriendshipsAsString(E friendship) {
        Friendship fr = (Friendship) friendship;
        String friendships = fr.getFirstUser().getId() +
                ";" +
                fr.getFirstUser().getFirstName() +
                ";" +
                fr.getFirstUser().getLastName() +
                ";" +
                fr.getSecondUser().getId() +
                ";" +
                fr.getSecondUser().getFirstName() +
                ";" +
                fr.getSecondUser().getLastName() +
                ";" +
                fr.getDate();
        return friendships;
    }

    /**
     *
     * @param attributes The attributes of the users
     */
    public void addFriendshipsFromFile(List<String> attributes) throws IOException {
        User user1 = new User(attributes.get(1), attributes.get(2));
        user1.setId(Long.parseLong(attributes.get(0)));
        User user2 = new User(attributes.get(4), attributes.get(5));
        user2.setId(Long.parseLong(attributes.get(3)));
        LocalDateTime date = LocalDateTime.parse(attributes.get(6));
        Friendship friendship = new Friendship(user1, user2);
        friendship.setDate(date);
        super.addFriendshipRepo((E) friendship);
    }

    /**
     *
     * @param friendship The friendship to be added in the repository
     * @throws IOException if it can't be saved
     */
    @Override
    public void addFriendshipRepo(E friendship) throws IOException {
        super.addFriendshipRepo(friendship);
        writeFriendships();
    }

    /**
     *
     * @param friendship The friendship to be deleted from the repository
     * @throws IOException if it can't be saved
     */
    @Override
    public void deleteFriendshipRepo(E friendship) throws IOException {
        super.deleteFriendshipRepo(friendship);
        writeFriendships();
    }

    /**
     *
     * @return A list consisting in all the friendships found in the repository
     */
    @Override
    public List<E> findAllFriendships() {
        return super.findAllFriendships();
    }

    /**
     *
     * @param friendship The friendship to be searched for in the repository
     * @return null if the friendship does not exist in the repo, friendship otherwise
     */
    @Override
    public E findOneFriendship(E friendship) {
        return super.findOneFriendship(friendship);
    }

    /**
     *
     * @param oldFriendship The friendship to be replaced
     * @param newFriendship The friendship that will replace the oldFriendship
     * @throws IOException if it can't be saved
     */
    @Override
    public void updateFriendship(E oldFriendship, E newFriendship) throws IOException {
        super.updateFriendship(oldFriendship, newFriendship);
        writeFriendships();
    }
}
