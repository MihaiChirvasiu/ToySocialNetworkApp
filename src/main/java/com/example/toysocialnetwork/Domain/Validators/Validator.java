package com.example.toysocialnetwork.Domain.Validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}
