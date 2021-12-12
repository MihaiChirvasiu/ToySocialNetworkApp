package com.example.toysocialnetwork.Domain.Validators;

import com.example.toysocialnetwork.Domain.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidator implements Validator<User> {
    /**
     *
     * @param entity The entity that has to be validated
     * @throws ValidationException The exception that is thrown if the entity is not valid
     */
    @Override
    public void validate(User entity) throws ValidationException {
        if(entity.getId() == null)
            throw new ValidationException("ID cannot be null");
        if(entity.getFirstName().equals(""))
            throw new ValidationException("First name cannot be null");
        if(entity.getLastName().equals(""))
            throw  new ValidationException("Last name cannot be null");
        Pattern namePattern = Pattern.compile("[0-9!@#$%^&*()_+{}|]");
        Matcher firstNameMatcher = namePattern.matcher(entity.getFirstName());
        if(firstNameMatcher.find())
            throw new ValidationException("First name is invalid");
        Matcher lastNameMatcher = namePattern.matcher(entity.getLastName());
        if(lastNameMatcher.find())
            throw new ValidationException("Last name is invalid");
        if(entity.getFirstName().contains("1") || entity.getFirstName().contains("2") || entity.getFirstName().contains("3") ||
                entity.getFirstName().contains("4") || entity.getFirstName().contains("5") || entity.getFirstName().contains("6") ||
                entity.getFirstName().contains("7") || entity.getFirstName().contains("8") || entity.getFirstName().contains("9") || entity.getFirstName().contains("0"))
            throw  new ValidationException("First name cannot contain digits");
        if(entity.getLastName().contains("1") || entity.getLastName().contains("2") || entity.getLastName().contains("3") ||
                entity.getLastName().contains("4") || entity.getLastName().contains("5") || entity.getLastName().contains("6") ||
                entity.getLastName().contains("7") || entity.getLastName().contains("8") || entity.getLastName().contains("9") || entity.getLastName().contains("0"))
            throw new ValidationException("Last name cannot contain digits");
    }
}
