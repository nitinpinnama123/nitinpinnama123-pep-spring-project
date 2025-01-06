package com.example.controller;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public @ResponseBody ResponseEntity<Account> registerUser (@RequestParam String username, @RequestParam String password) {
        Account a = new Account(username, password);
        if (a.getUsername() == null || a.getUsername().length() == 0)
        {
            return ResponseEntity.status(400).body(a);
        }
        else if (a.getPassword() != null && a.getPassword().length() < 4)
        {
            return ResponseEntity.status(400).body(a);
        }
        else if (getAccountByUsername(a.getUsername()) == null)
        {
           registerUser(a.getUsername(), a.getPassword());
           accounts.add(a);
           return ResponseEntity.status(200).body(a);
        }
        else {
           return ResponseEntity.status(400).body(a);
        }
        
    }

    @PostMapping("/login")
    public @ResponseBody ResponseEntity<Boolean> loginUser (@RequestParam String username, @RequestParam String password) {

    }

    @PostMapping("/messages")
    public @ResponseBody ResponseEntity<Message> createMessage(@RequestParam int posted_by, @RequestParam String message_text, @RequestParam long time_posted_epoch) {
        Message m = new Message(posted_by, message_text, time_posted_epoch);
        if (m.getMessageText() == null || m.getMessageText().length() == 0 || m.getMessageText().length() > 255) {
            return ResponseEntity.status(400).body(m);
        }
        else if (getAccountById(m.getPostedBy()) == null)
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
    }


    @GetMapping("/messages/{message_id}")
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

    @DeleteMapping("/messages/{message_id}")
    public Message deleteMessageById(@PathVariable int message_id)
    {

    }

    @PatchMapping("/messages/{message_id}")
    public Message updateMessageTextById(@PathVariable int message_id, @RequestParam String str)
    {

    }

    @GetMapping("/accounts/{account_id}/messages")
    public List<Message> getAllMessagesByUsers(@PathVariable int account_id)
    {
        
    }

    @GetMapping("/accounts/{username}")
    public @ResponseBody Account getAccountByUsername(@PathVariable String username)
    {
        for (Account acc : accounts)
        {
            if (acc.getUsername().equals(username))
            {
                return acc;
            }
        }
        return null;
    }
        






}
