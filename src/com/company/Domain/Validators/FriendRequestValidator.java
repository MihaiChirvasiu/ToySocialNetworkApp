package com.company.Domain.Validators;

import com.company.Domain.FriendRequest;

import java.util.Objects;

public class FriendRequestValidator implements Validator<FriendRequest>{

    @Override
    public void validate(FriendRequest entity) throws ValidationException {
        if(entity.getUser1()==null || entity.getUser2()==null)
            throw new ValidationException("User cannot be null!");
        if(Objects.equals(entity.getUser1().getId(), entity.getUser2().getId()))
            throw new ValidationException("Cannot send a friend request to itself!");
    }
}
