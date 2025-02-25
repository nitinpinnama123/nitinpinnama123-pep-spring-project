package com.example.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

import Util.ConnectionUtil;

@Service
@Transactional
public class AccountService {

    AccountRepository accRepository;

    @Autowired
    public AccountService(AccountRepository accRepository) {
        this.accRepository = accRepository;
    }

    public Account registerUser(Account acc)
    {
        return accRepository.save(acc);
    }

    public Account getAccountById(Integer id)
    {
        //Optional<Account> optionalAccount = accRepository.findById(new Long((long)id.intValue()));
        Optional<Account> optionalAccount = accRepository.findById(id);
        if (optionalAccount.isPresent())
        {
            return optionalAccount.get();
        }
        else {
            return null;
        }
    }

    public List<Account> getAllAccounts()
    {
        return accRepository.findAll();
    }

    public Account getAccountByUsername(String username)
    {
        return accRepository.findAccountByUsername(username);
    }

    public Account login(String username, String password)
    {
        return accRepository.findAccountByUsernameAndPassword(username, password);
    }


}
