package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    // Named queries
    @Query("SELECT a FROM Account a WHERE a.username = :username")
    Account findAccountByUsername(String username);

    @Query("SELECT a FROM Account a WHERE a.username = :username AND a.password = :password")
    Account findAccountByUsernameAndPassword(String username, String password);


}
