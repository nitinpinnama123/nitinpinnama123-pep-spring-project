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

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;

import ch.qos.logback.core.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@Controller
@ResponseBody
public class SocialMediaController {
    private List<Account> accounts = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

    @PostMapping("/register")
    public @ResponseBody ResponseEntity<Account> registerUser (@RequestParam String username, @RequestParam String password) throws SQLException  {
        Account accCreated = null;
        Account a = new Account(username, password);
        AccountService accService = AccountService.instance();
        if (a.getUsername() == null || a.getUsername().length() == 0)
        {
            return ResponseEntity.status(400).body(a);
        }
        else if (a.getPassword() != null && a.getPassword().length() < 4)
        {
            return ResponseEntity.status(400).body(a);
        }
        else if (accService.getAccountByUsername(a.getUsername()) == null)
        {
           accCreated = accService.registerUser(a.getUsername(), a.getPassword());
           return ResponseEntity.status(200).body(accCreated);
        }
        else {
           return ResponseEntity.status(400).body(accCreated);
        }
        
    }

    @PostMapping("/login")
    public @ResponseBody ResponseEntity<Account> loginUser (@RequestParam String username, @RequestParam String password) throws SQLException {
        Account acc = new Account(username, password);
        Account accFetched = null;
        AccountService accService = AccountService.instance();
        if (acc.getUsername() == null || acc.getUsername().length() == 0
        || acc.getPassword() == null || acc.getPassword().length() == 0)
        {
            return ResponseEntity.status(401).body(acc);
        }
        else {
            accFetched = accService.getAccountByUsername(acc.getUsername());
            if (accFetched != null)
            {
                if (acc.getPassword().equals(accFetched.getPassword()))
                {
                    return ResponseEntity.status(200).body(accFetched);
                }
                else {
                    return ResponseEntity.status(401).body(accFetched);
                }
            }
            else {
                return ResponseEntity.status(401).body(accFetched);
            }
        }

    }

    @PostMapping("/messages")
    public @ResponseBody ResponseEntity<Message> createMessage(@RequestParam int posted_by, @RequestParam String message_text, @RequestParam long time_posted_epoch) throws SQLException {
        Message m = new Message(posted_by, message_text, time_posted_epoch);
        AccountService accService = AccountService.instance();
        if (m.getMessageText() == null || m.getMessageText().length() == 0 || m.getMessageText().length() > 255) {
            return ResponseEntity.status(400).body(m);
        }
        else if (accService.getAccountById(m.getPostedBy()) == null)
        {
            return ResponseEntity.status(400).body(m);
        }
        else {
            createMessage(m.getPostedBy(), m.getMessageText(), m.getTimePostedEpoch());
            messages.add(m);
            return ResponseEntity.status(200).body(m);
        }
        

    }

    @GetMapping("/messages")
    public @ResponseBody ResponseEntity<List<Message>> getAllMessages() {
        //return ResponseEntity.status(200).body(mess)
        return ResponseEntity.status(200).body(messages);
    }


    @GetMapping("/messages/{messageId}")
    public @ResponseBody ResponseEntity<Message> getMessageById(@PathVariable int message_id)
    {
        for (Message m : messages) {
            if (m.getMessageId() == message_id)
            {
                return ResponseEntity.status(200).body(m);
            }
        }
        return null;
    }

    @DeleteMapping("/messages/{messageId}")
    public @ResponseBody ResponseEntity<Message> deleteMessageById(@PathVariable int message_id)
    {
        ResponseEntity<Message> mDeleted = null;
        if (messages.removeIf(message -> message.getMessageId().equals(message_id))) {
            mDeleted = getMessageById(message_id);
            messages.remove(mDeleted.getBody());
        }
        if (mDeleted == null)
        {
            return ResponseEntity.status(200).body(mDeleted.getBody());
        }
        else {
            return ResponseEntity.status(200).body(mDeleted.getBody());
        }

        
    }

    @PatchMapping("/messages/{messageId}")
    public @ResponseBody ResponseEntity<Message> updateMessageTextById(@PathVariable int message_id, @RequestParam String str)
    {

        ResponseEntity<Message> messageUpdated = null;
        for (Message m : messages)
        {
            if (m.getMessageId() == message_id)
            {
                if (getMessageById(message_id) != null)
                {
                    if (m.getMessageText() == null || m.getMessageText().length() == 0 || m.getMessageText().length() > 255)
                    {
                        return ResponseEntity.status(400).body(m);
                    }
                    else {
                        messageUpdated = updateMessageTextById(message_id, str);
                        return ResponseEntity.status(200).body(messageUpdated.getBody());
                    }
                }
                else {
                    return ResponseEntity.status(400).body(m);
                }
            }
        }
        return messageUpdated;
    }

    @GetMapping("/accounts/{accountId}/messages")
    public @ResponseBody ResponseEntity<List<Message>> getAllMessagesByUsers(@PathVariable int account_id)
    {
        List<Message> messagesByUser = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++)
        {
            if (messages.get(i).getPostedBy() == account_id)
            {
                messagesByUser.add(messages.get(i));
            }
        }
        return ResponseEntity.status(200).body(messagesByUser);

    }

    @GetMapping("/accounts/{username}")
    public @ResponseBody Account getAccountByUsername(@PathVariable String username) throws SQLException
    {
        AccountService accService = AccountService.instance();
        Account accountToGet = accService.getAccountByUsername(username);
        for (Account acc : accounts)
        {
            if (acc.getUsername().equals(accountToGet.getUsername()))
            {
                return acc;
            }
        }
        return null;
    }

    @GetMapping("/accounts/{accountId}")
    public @ResponseBody Account getAccountById(@PathVariable int account_id) throws SQLException
    {
        AccountService accService = AccountService.instance();
        Account accountToGet = accService.getAccountById(account_id);
        for (Account acc : accounts)
        {
            if (acc.getAccountId() == accountToGet.getAccountId())
            {
                return acc;
            }
        }
        return null;
    }
        






}
