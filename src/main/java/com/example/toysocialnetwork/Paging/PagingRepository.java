package com.example.toysocialnetwork.Paging;

import com.example.toysocialnetwork.Domain.Entity;
import com.example.toysocialnetwork.Repository.UserRepository;

import java.sql.SQLException;

public interface PagingRepository<ID , E extends Entity<ID>>  {

    Page<E> findAll(Pageable pageable) throws SQLException;
}
