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
import com.example.service.MessageService;

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
    private final AccountService accService;
    private final MessageService msgService;

    public SocialMediaController(AccountService accService, MessageService msgService)
    {
        this.accService = accService;
        this.msgService = msgService;
    }
    private List<Account> accounts = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

    @PostMapping("/register")
    public @ResponseBody ResponseEntity<Account> registerUser (@RequestParam String username, @RequestParam String password)  {
        Account accCreated = new Account(username, password);
        /*Account accCreated = null;
        Account a = new Account(username, password);
        AccountService accService = AccountService.instance();*/
        if (accCreated.getUsername() == null || accCreated.getUsername().length() == 0)
        {
            return ResponseEntity.status(400).body(accCreated);
        }
        else if (accCreated.getPassword() != null && accCreated.getPassword().length() < 4)
        {
            return ResponseEntity.status(400).body(accCreated);
        }
        else if (accService.getAccountByUsername(accCreated.getUsername()) == null)
        {
           Account acc = accService.registerUser(accCreated);
           return ResponseEntity.status(200).body(acc);
        }
        else {
           return ResponseEntity.status(400).body(accCreated);
        }
        
    }

    @PostMapping("/login")
    public @ResponseBody ResponseEntity<Account> loginUser (@RequestParam String username, @RequestParam String password) {
        Account acc = new Account(username, password);
        Account accFetched = null;
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
                    accService.login(username, password);
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
    public @ResponseBody ResponseEntity<Message> createMessage(@RequestParam int posted_by, @RequestParam String message_text, @RequestParam long time_posted_epoch) {
        Message m = new Message(posted_by, message_text, time_posted_epoch);
        if (m.getMessageText() == null || m.getMessageText().length() == 0 || m.getMessageText().length() > 255) {
            return ResponseEntity.status(400).body(m);
        }
        else if (accService.getAccountById(m.getPostedBy()) == null)
        {
            return ResponseEntity.status(400).body(m);
        }
        else {
            Message mAdded = msgService.createMessage(new Message(m.getPostedBy(), m.getMessageText(), m.getTimePostedEpoch()));
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
    public @ResponseBody ResponseEntity<Message> getMessageById(@PathVariable long message_id) throws SQLException
    {
        /*MessageService msgService = MessageService.instance();
        Message messageToGet = msgService.getMessageById(message_id);
       if (messageToGet != null)
       {
        return ResponseEntity.status(200).body(messageToGet);
       }
       else {
        return ResponseEntity.status(200).body(messageToGet);
       }*/
      Message messageToGet = msgService.getMessageById(message_id);
      return ResponseEntity.status(200).body(messageToGet);
    }

    @DeleteMapping("/messages/{messageId}")
    public @ResponseBody ResponseEntity<Message> deleteMessageById(@PathVariable int message_id) throws SQLException
    {
        if (messages.removeIf(message -> message.getMessageId().equals(message_id))) {
            msgService.deleteMessageById(message_id);
        }
        return ResponseEntity.status(200).body(msgService.getMessageById(message_id));

        
    }

    @PatchMapping("/messages/{messageId}")
    public @ResponseBody ResponseEntity<Message> updateMessageTextById(@PathVariable int message_id, @RequestParam String str) throws SQLException
    {
        Message m = null;
        for (Message msg : messages)
        {
            if (msg.getMessageId() == message_id)
            {
                m = msg;
            }
        }
        if (msgService.getMessageById(message_id) != null)
        {
            if (m.getMessageText() == null || m.getMessageText().length() == 0 || m.getMessageText().length() > 255)
            {
                return ResponseEntity.status(400).body(m);
            }
            else {
                Message mReturn = msgService.updateMessageTextById(message_id, str);
                return ResponseEntity.status(200).body(mReturn);
            }
        }
        else {
            return ResponseEntity.status(400).body(msgService.getMessageById(message_id));
        }
    
    }

    @GetMapping("/accounts/{accountId}/messages")
    public @ResponseBody ResponseEntity<List<Message>> getAllMessagesByUsers(@PathVariable int account_id) throws SQLException
    {
        List<Message> messagesByUser = msgService.getAllMessagesByUser(account_id);
        return ResponseEntity.status(200).body(messagesByUser);

    }

    @GetMapping("/accounts/{username}")
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
    }

    @GetMapping("/accounts/{accountId}")
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
    }
        






}
