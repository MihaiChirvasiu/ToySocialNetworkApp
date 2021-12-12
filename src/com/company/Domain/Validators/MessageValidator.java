package com.company.Domain.Validators;

import com.company.Domain.FriendRequest;
import com.company.Domain.Message;

import java.util.Objects;

public class MessageValidator implements Validator<Message>{

    /**
     *
     * @param entity The message to be validated
     * @throws ValidationException if the entity is not a valid one
     */
    @Override
    public void validate(Message entity) throws ValidationException {
        if(entity.getFromUser()==null)
            throw new ValidationException("User cannot be null!");
        for(int i = 0; i < entity.getToUsers().size(); i++){
            if(Objects.equals(entity.getToUsers().get(i).getId(), entity.getFromUser().getId()))
                throw new ValidationException("Cannot send a message to itself!");
        }
    }
}
