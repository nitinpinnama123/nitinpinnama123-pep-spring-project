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
            // Password is null or password length is < 4; return 400
            return ResponseEntity.status(400).body(acc);
        }
        else if (accService.getAccountByUsername(acc.getUsername()) != null)
        {
            // User already exists; return 409
            return ResponseEntity.status(409).body(acc);
        }
        else {
            // User doesn't exist, create account and return 200
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
            // Login with credentials
            accFetched = accService.login(acc.getUsername(), acc.getPassword());
            if (accFetched != null)
            {
                // Account exists (valid credentials), return 200
                return ResponseEntity.status(200).body(accFetched);
            }
            else {
                // Account does not exist (invalid credentials), return 401
                return ResponseEntity.status(401).body(accFetched);
            }
        }

    }

    @PostMapping("/messages")
    public @ResponseBody ResponseEntity<Message> createMessage(@RequestBody Message m) {

        //Message m = new Message(posted_by, message_text, time_posted_epoch);
        if (m.getMessageText() == null || m.getMessageText().length() == 0 || 
            m.getMessageText().length() > 255) {
            // Message text is empty or > 255, return 400
            return ResponseEntity.status(400).body(m);
        }
        else if (accService.getAccountById(m.getPostedBy()) == null)
        {
            // posting user ID does not exist, return 400
            return ResponseEntity.status(400).body(m);
        }
        else {
            // Message provided is good, save it and return 200
            Message mAdded = msgService.createMessage(m);
            return ResponseEntity.status(200).body(mAdded);
        }
        

    }

    @GetMapping("/messages")
    public @ResponseBody ResponseEntity<List<Message>> getAllMessages() {

        List<Message> messages = msgService.getAllMessages(); // All messages
        return ResponseEntity.status(200).body(messages); // return 200 whether or not there are any messages
    }


    @GetMapping("/messages/{messageId}")
    public @ResponseBody ResponseEntity<Message> getMessageById(@PathVariable int messageId) throws SQLException
    {
      Message messageToGet = msgService.getMessageById(messageId); // Get message by id
      return ResponseEntity.status(200).body(messageToGet); // return 200  
    }

    @DeleteMapping("/messages/{messageId}")
    public @ResponseBody ResponseEntity<String> deleteMessageById(@PathVariable int messageId) throws SQLException
    {
        if (msgService.getMessageById(messageId) != null) { 
            // Message exists, delete it and respond with 1
            msgService.deleteMessageById(messageId);
            return ResponseEntity.status(200).body("1");
        }
        // Given message does not exist, nothing to do, return success (200)
        return ResponseEntity.status(200).body("");
    }

    @PatchMapping("/messages/{messageId}")
    public @ResponseBody ResponseEntity<String> updateMessageTextById(@PathVariable int messageId, @RequestBody Message mProvided) throws SQLException
    {

        Message m = msgService.getMessageById(messageId); // Get message with given messageID
        if (m != null)
        {
            // Message with given ID exists
            if (mProvided.getMessageText() == null || mProvided.getMessageText().length() == 0 || 
                mProvided.getMessageText().length() > 255)
            {
                // Given message is empty or > 255, return 400
                return ResponseEntity.status(400).body("Invalid message");
            }
            else {
                // Given message is valid length, update message text with provided text and return 200
                msgService.updateMessageTextById(messageId, mProvided.getMessageText());
                return ResponseEntity.status(200).body("1");
                //return new ResponseEntity<String>("1", HttpStatus.OK);
            }
        }
        else { // Message with given id does not exist, return 400
            return ResponseEntity.status(400).body("Message id does not exist");
        }
    
    }

    @GetMapping("/accounts/{accountId}/messages")
    public @ResponseBody ResponseEntity<List<Message>> getAllMessagesByUsers(@PathVariable int accountId) throws SQLException
    {
        List<Message> messagesByUser = msgService.getAllMessagesByUser(accountId); // Get all messages by the user with given accountID
        return ResponseEntity.status(200).body(messagesByUser); // return 200

    }

    
        






}
