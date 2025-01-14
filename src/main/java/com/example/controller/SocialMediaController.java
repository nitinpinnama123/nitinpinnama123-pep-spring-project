package com.example.controller;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

import ch.qos.logback.core.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */


@RestController
public class SocialMediaController {
    private final AccountService accService;
    private final MessageService msgService;

    public SocialMediaController(AccountService accService, MessageService msgService)
    {
        this.accService = accService;
        this.msgService = msgService;
    }


    @PostMapping("/register")
    public @ResponseBody ResponseEntity<Account> registerUser (@RequestBody Account acc)  {

        if (acc.getUsername() == null || acc.getUsername().length() == 0)
        {
            // Username is missing; return 400
            return ResponseEntity.status(400).body(acc);
        }
        else if (acc.getPassword() == null || acc.getPassword().length() < 4)
        {
            // Password is null or password length is < 4
            return ResponseEntity.status(400).body(acc);
        }
        else if (accService.getAccountByUsername(acc.getUsername()) != null)
        {
            // User already exists
            return ResponseEntity.status(409).body(acc);
        }
        else {
            // User doesn't exist, create account
           Account acct = accService.registerUser(acc);
           return ResponseEntity.status(200).body(acct);
        }
        
    }

    @PostMapping("/login")
    public @ResponseBody ResponseEntity<Account> loginUser (@RequestBody Account acc) {

        Account accFetched = null;
        if (acc.getUsername() == null || acc.getUsername().length() == 0
        || acc.getPassword() == null || acc.getPassword().length() == 0)
        {
            // username or password is null, return 401
            return ResponseEntity.status(401).body(acc);
        }
        else {
            accFetched = accService.login(acc.getUsername(), acc.getPassword());
            if (accFetched != null)
            {
                return ResponseEntity.status(200).body(accFetched);
            }
            else {
                return ResponseEntity.status(401).body(accFetched);
            }
        }

    }

    @PostMapping("/messages")
    public @ResponseBody ResponseEntity<Message> createMessage(@RequestBody Message m) {
        //Message m = new Message(posted_by, message_text, time_posted_epoch);
        if (m.getMessageText() == null || m.getMessageText().length() == 0 || m.getMessageText().length() > 255) {
            // Message text is empty or > 255
            return ResponseEntity.status(400).body(m);
        }
        else if (accService.getAccountById(m.getPostedBy()) == null)
        {
            // posting user ID does not exist
            return ResponseEntity.status(400).body(m);
        }
        else {
            // 
            Message mAdded = msgService.createMessage(m);
            return ResponseEntity.status(200).body(mAdded);
        }
        

    }

    @GetMapping("/messages")
    public @ResponseBody ResponseEntity<List<Message>> getAllMessages() {
        //return ResponseEntity.status(200).body(mess)
        List<Message> messages = msgService.getAllMessages();
        return ResponseEntity.status(200).body(messages);
    }


    @GetMapping("/messages/{messageId}")
    public @ResponseBody ResponseEntity<Message> getMessageById(@PathVariable int messageId) throws SQLException
    {
      Message messageToGet = msgService.getMessageById(messageId);
      return ResponseEntity.status(200).body(messageToGet);
    }

    @DeleteMapping("/messages/{messageId}")
    public @ResponseBody ResponseEntity<Message> deleteMessageById(@PathVariable int messageId) throws SQLException
    {
        if (msgService.getMessageById(messageId) != null) {
            msgService.deleteMessageById(messageId);
            return ResponseEntity.status(200).body(msgService.getMessageById(messageId));
        }
        return ResponseEntity.status(200).body(msgService.getMessageById(messageId));

        
    }

    @PatchMapping("/messages/{messageId}")
    public @ResponseBody ResponseEntity<Message> updateMessageTextById(@PathVariable int messageId, @RequestParam String str) throws SQLException
    {
        /*Message m = null;
        for (Message msg : messages)
        {
            if (msg.getMessageId() == message_id)
            {
                m = msg;
            }
        }*/

        Message m = msgService.getMessageById(messageId);
        if (m != null)
        {
            if (m.getMessageText() == null || m.getMessageText().length() == 0 || m.getMessageText().length() > 255)
            {
                return ResponseEntity.status(400).body(m);
            }
            else {
                msgService.updateMessageTextById(messageId, str);
                return ResponseEntity.status(200).body(m);
            }
        }
        else {
            return ResponseEntity.status(400).body(m);
        }
    
    }

    @GetMapping("/accounts/{accountId}/messages")
    public @ResponseBody ResponseEntity<List<Message>> getAllMessagesByUsers(@PathVariable int accountId) throws SQLException
    {
        List<Message> messagesByUser = msgService.getAllMessagesByUser(accountId);
        return ResponseEntity.status(200).body(messagesByUser);

    }

    /*@GetMapping("/accounts/{username}")
    public @ResponseBody Account getAccountByUsername(@PathVariable String username) throws SQLException
    {
    
        Account accountToGet = accService.getAccountByUsername(username);
        for (Account acc : accounts)
        {
            if (acc.getUsername().equals(accountToGet.getUsername()))
            {
                return acc;
            }
        }
        return null;
    }*/

    /*@GetMapping("/accounts/{accountId}")
    public @ResponseBody Account getAccountById(@PathVariable int account_id) throws SQLException
    {
        Account accountToGet = accService.getAccountById(account_id);
        for (Account acc : accounts)
        {
            if (acc.getAccountId() == accountToGet.getAccountId())
            {
                return acc;
            }
        }
        return null;
    }*/
        






}
