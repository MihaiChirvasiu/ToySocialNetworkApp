package com.company.Domain.Validators;

import com.company.Domain.Friendship;

import java.util.Objects;

public class FriendshipValidator implements Validator<Friendship> {

    /**
     *
     * @param entity The friendship to be validated
     * @throws ValidationException if the friendship is not a valid one
     */
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(Objects.equals(entity.getFirstUser().getId(), entity.getSecondUser().getId())){
            throw new ValidationException("Can't assign the same user as friend");
        }
        if(entity == null){
            throw new ValidationException("Friendship can't be null");
        }
    }
}
