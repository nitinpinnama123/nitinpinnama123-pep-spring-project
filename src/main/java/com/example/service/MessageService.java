package com.example.service;

import static org.springframework.boot.SpringApplication.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

import Util.ConnectionUtil;

@Service
public class MessageService {
    MessageRepository msgRepository;
    private static MessageService msgService = null;

    @Autowired
    public MessageService(MessageRepository msgRepository)
    {
        this.msgRepository = msgRepository;
    }

    // private List<Message> messages = new ArrayList<Message>();

    @PostMapping("/messages")
    public Message createMessage(Message m) {
        return msgRepository.save(m);
    }

    @GetMapping("/messages/{messageId}")
    public Message getMessageById(long id)
    {
        return msgRepository.findById(id).orElse(null);
    }
    
    @GetMapping("/messages")
    public @ResponseBody List<Message> getAllMessages()
    {
        return msgRepository.findAll();
    }

    public void deleteMessageById(long id) throws SQLException
    {
        msgRepository.deleteById(id);
    }
    
    public Message updateMessageTextById(long id, String str)
    {
        return msgRepository.findById(id).map(msg -> {
            msg.setMessageText(str);
            return msgRepository.save(msg);
        }).orElse(null);
        
    }
     
}
