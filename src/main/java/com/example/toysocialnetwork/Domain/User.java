package com.example.toysocialnetwork.Domain;

import java.util.Objects;

public class User extends Entity<Long>{
    private String firstName;
    private String lastName;
    private String password;
    private String email;

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    /**
     * Getter for the email of the user
     * @return the email of the user
     */
    public String getEmail(){
        return email;
    }

    /**
     * Getter for the password of the user
     * @return the password of the user
     */
    public String getPassword(){
        return password;
    }

    /**
     * Setter for a new password
     * @param newPassword the new password
     */
    public void setPassword(String newPassword){
        this.password = newPassword;
    }

    /**
     * Setter for the email
     * @param newEmail the new email to be set
     */
    public void setEmail(String newEmail){
        this.email = email;
    }

    /**
     * Getter for the FirstName of a USer
     * @return firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName The String to be set as the firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter for the LastName of a User
     * @return lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName The String to be set as the lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /*
    @Override
    public String toString() {
        return "Utilizator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + friends +
                '}';
    }

     */

    /**
     *
     * @param o A User
     * @return true if the objects are the same, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName());
    }

    /**
     *
     * @return the HashCode of a User
     */
    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName());
    }
}
