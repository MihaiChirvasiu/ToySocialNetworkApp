package com.company.Domain;

import com.company.Repository.FriendshipRepository;
import com.company.Repository.UserRepository;

import java.sql.SQLException;
import java.util.*;

public class Network<ID, E extends Entity<ID>, E1 extends Entity<ID>> {

    private LinkedList<Long>[] adj;
    private Long[] connectedComponents;
    private UserRepository<ID, E> repository;
    private FriendshipRepository<ID, E1> friendshipRepository;

    /**
     *
     * @param size The dimension of the network based on the keys
     * @param keys The vertices of the graph
     * @param repository The repository where the users are stored
     */
    public Network(int size, Set<ID> keys, UserRepository<ID, E> repository, FriendshipRepository<ID, E1> friendshipRepository) throws SQLException {
        this.repository = repository;
        this.friendshipRepository = friendshipRepository;
        long dim = size;
        adj = new LinkedList[(int) (dim + 2)];
        for(ID k : keys){
            adj[Math.toIntExact((Long) k)] = new LinkedList<>();
        }
        for(E1 k : friendshipRepository.findAllFriendships()) {
            Friendship friendship = (Friendship) k;
            Long idUser1 = friendship.getFirstUser().getId();
            Long idUser2 = friendship.getSecondUser().getId();
            adj[Math.toIntExact(idUser1)].add(idUser2);
            adj[Math.toIntExact(idUser2)].add(idUser1);
        }
    }

    /**
     *
     * @param adj The adjacency list that represents the graph of friendships
     * @param viz The array that determines what vertices were visited
     * @param connectedComponents The array of connected components
     * @param source The node from where we will start our search
     * @param nrc The number of connected components
     */
    private void BFS(LinkedList<Long>[] adj, List<Long> viz, Long[] connectedComponents, long source, long nrc){
        Queue<Long> Q = new LinkedList<>();
        viz.add(source);
        this.connectedComponents[(int) source] = nrc;
        Q.add(source);
        while(!Q.isEmpty()){
            long currentNode = Q.peek();
            Q.remove();
            for(int i = 0; i < adj[(int) currentNode].size(); i++){
                if(adj[(int) currentNode].get(i) != null && !viz.contains(adj[(int) currentNode].get(i))){
                    Q.add(adj[(int) currentNode].get(i));
                    viz.add(adj[(int) currentNode].get(i));
                    this.connectedComponents[Math.toIntExact(adj[(int) currentNode].get(i))] = nrc;
                }
            }
        }
    }

    /**
     *
     * @param keys The vertices of the graph
     * @param maxx The last key of the graph
     * @return The number of communities(the number of connected components)
     */
    public int numberOfCommunities(Set<ID> keys, int maxx){
        List<Long> viz = new ArrayList<>();
        int nrc = 0;
        this.connectedComponents = new Long[(int) (maxx + 2)];
        for(ID k : keys){
            if(!viz.contains((Long) k)) {
                nrc++;
                BFS(adj, viz, connectedComponents, (Long) k, nrc);
            }
        }
        return nrc;
    }

    /**
     *
     * @param k The last position in the solution array
     * @param maxLength The values of the longest road
     * @param actualRoad The array which will contain the longest road in a community
     * @param s The solution array that will contain all the roads
     * @return The longest road determined
     */
    private Long[] findLongestRoad(int k, int[] maxLength, Long[] actualRoad ,Long[] s){
        //for(int i = 0; i <= k; i++)
        //System.out.print(s[i] + " ");
        //System.out.println();

        if(k > maxLength[0]){
            maxLength[0] = k;
            for(int i = 0; i <= k; i++)
                actualRoad[i] = s[i];
            return actualRoad;
        }
        return actualRoad;
    }

    /**
     *
     * @param k The last position in the solution array
     * @param adj The adjacency list of the graph
     * @param s The solution array that will contain all the roads in a community
     * @return 1 if the road is a valid one or 0 otherwise
     */
    private int ok(int k, LinkedList<Long>[] adj, Long[] s){
        int ok = 0;
        for(int i = 0; i < k; i++){
            for(int j = i + 1; j <= k; j++){
                if(Objects.equals(s[i], s[j]))
                    return 0;
            }
        }
        for(int i = 0; i < k; i++){
            if(!adj[Math.toIntExact(s[i + 1])].contains(s[i]))
                return 0;
        }
        return 1;
    }

    /**
     *
     * @param k The current position in the solution array
     * @param index The number of vertices
     * @param maxLength The maximum length of a road
     * @param actualRoad The array that will contain the longest road
     * @param potentialRoad The array that will contain the vertices in a community
     * @param adj The adjacency list of the graph
     * @param s The solution array that will have all the possible road in a community
     */
    private void back(int k, int index, int[] maxLength, Long[] actualRoad, Long[] potentialRoad, LinkedList<Long>[] adj, Long[] s){
        for(int i = 0; i < index; i++){
            s[k] = potentialRoad[i];
            if(ok(k, adj, s) == 1)
                actualRoad = findLongestRoad(k, maxLength, actualRoad, s);
            if(k < index) {
                back(k + 1, index, maxLength, actualRoad, potentialRoad, adj, s);
            }
        }
    }

    /**
     *
     * @param keys The vertices of the graph
     * @param maxx The maximum value of a node
     * @return The array that will contain the longest road from a community
     */
    public Long[] longestRoadInACommunity(Set<ID> keys, int maxx){
        int nrc = numberOfCommunities(keys, maxx);
        Long[] actualRoad = new Long[(int) (maxx + 2)];
        int[] maxLength = {0};
        for(int i = 1; i <= nrc; i++){
            Long[] potentialRoad = new Long[(int) (maxx + 2)];
            Long[] s = new Long[(int) (maxx + 2)];
            int index = 0;
            for(ID k : keys){
                if(connectedComponents[Math.toIntExact((Long) k)] == i)
                    potentialRoad[index++] = (Long) k;
            }
            back(0, index, maxLength, actualRoad, potentialRoad, adj, s);
        }
        return actualRoad;
    }

}

