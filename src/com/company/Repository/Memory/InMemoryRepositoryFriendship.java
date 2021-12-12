package com.company.Repository.Memory;

import com.company.Domain.Entity;
import com.company.Domain.Friendship;
import com.company.Domain.User;
import com.company.Domain.Validators.Validator;
import com.company.Repository.FriendshipRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InMemoryRepositoryFriendship<ID, E extends Entity<ID>> implements FriendshipRepository<ID, E> {

    private List<Friendship> friendships;
    Validator<E> validator;

    public InMemoryRepositoryFriendship(Validator<E> validator){
        this.validator = validator;
        this.friendships = new ArrayList<>();
    }

    /**
     *
     * @param friendship The friendship to be added in the repository
     * @throws IOException Inheritance(file)
     */
    @Override
    public void addFriendshipRepo(E friendship) throws IOException {
        validator.validate(friendship);
        Friendship friendship1 = (Friendship) friendship;
        boolean found = false;
        for(int i = 0; i < friendships.size() && !found; i++) {
            if(Objects.equals(friendships.get(i).getFirstUser().getId(), friendship1.getFirstUser().getId()) &&
                    Objects.equals(friendships.get(i).getSecondUser().getId(), friendship1.getSecondUser().getId()))
                found = true;
        }
        if(!found)
            friendships.add(friendship1);
    }

    /**
     *
     * @param friendship The friendship to be deleted from the repository
     * @throws IOException Inheritance(file)
     */
    @Override
    public void deleteFriendshipRepo(E friendship) throws IOException {
        validator.validate(friendship);
        Friendship friendship1 = (Friendship) friendship;
        boolean found = false;
        for(int i = 0; i < friendships.size() && !found; i++) {
            if(Objects.equals(friendships.get(i).getFirstUser().getId(), friendship1.getFirstUser().getId()) &&
                    Objects.equals(friendships.get(i).getSecondUser().getId(), friendship1.getSecondUser().getId())){
                friendships.remove(i);
                found = true;
            }
        }
    }

    /**
     *
     * @return A list consisting of all the friendships found in the repository
     */
    @Override
    public List<E> findAllFriendships() {
        return (List<E>) this.friendships;
    }

    /**
     *
     * @param friendship The friendship to be searched for
     * @return null if the friendship does not exist, friendship otherwise
     */
    @Override
    public E findOneFriendship(E friendship) {
        validator.validate(friendship);
        Friendship friendship1 = (Friendship) friendship;
        boolean found = false;
        for(int i = 0; i < friendships.size() && !found; i++) {
            if(Objects.equals(friendships.get(i).getFirstUser().getId(), friendship1.getFirstUser().getId()) &&
                    Objects.equals(friendships.get(i).getSecondUser().getId(), friendship1.getSecondUser().getId()))
                found = true;
        }
        if(found)
            return friendship;
        else
            return null;
    }

    /**
     *
     * @param oldFriendship The friendship to be updated
     * @param newFriendship The friendship that will replace the oldFriendship
     * @throws IOException Inheritance(file)
     */
    @Override
    public void updateFriendship(E oldFriendship, E newFriendship) throws IOException {
        for(int i = 0; i < friendships.size(); i++){
            Friendship fr = (Friendship) oldFriendship;
            if(Objects.equals(friendships.get(i).getFirstUser().getId(), fr.getFirstUser().getId()) &&
                    Objects.equals(friendships.get(i).getSecondUser().getId(), fr.getSecondUser().getId())){
                Friendship friendship = (Friendship) newFriendship;
                friendships.get(i).setFirstUser(friendship.getFirstUser());
                friendships.get(i).setSecondUser(friendship.getSecondUser());
            }
        }
    }

    /**
     *
     * @return The number of friendships found in the repository
     */
    @Override
    public int getSize(){
        return friendships.size();
    }

    @Override
    public List<E> findAllFriendshipsForUser(ID idUser) throws SQLException {
        return null;
    }
}
