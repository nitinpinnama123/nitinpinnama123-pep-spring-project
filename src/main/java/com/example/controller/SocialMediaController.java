package com.example.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @PostMapping("/register")
    public Account registerUser (@RequestBody Account account) {
        
    }

    @PostMapping("/login")
    public Account loginUser (@RequestBody Account loginRequestAccount) {

    }

    @PostMapping("/messages")
    public Message createMessage(@RequestBody Message m) {

    }

    @GetMapping("/messages")
    public List<Message> getAllMessages() {

    }

    @GetMapping("/messages/{message_id}")
    public Message getMessageById(@PathVariable int message_id)
    {
        
    }

    @DeleteMapping("/messages/{message_id}")
    public Message deleteMessageById(@PathVariable int message_id)
    {

    }

    @PatchMapping("/messages/{message_id}")
    public Message updateMessageTextById(@PathVariable int message_id, @PathVariable String str)
    {

    }

    @GetMapping("/accounts/{account_id}/messages")
    public List<Message> getAllMessagesByUsers(@PathVariable int account_id)
    {
        
    }






}
