package com.example.service;

import static org.springframework.boot.SpringApplication.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.AccessOptions.SetOptions.Propagation;
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
import net.bytebuddy.dynamic.DynamicType.Builder.FieldDefinition.Optional;


@Service
@Transactional
public class MessageService {
    MessageRepository msgRepository;
    private static MessageService msgService = null;

    @Autowired
    public MessageService(MessageRepository msgRepository)
    {
        this.msgRepository = msgRepository;
    }

    // private List<Message> messages = new ArrayList<Message>();

    public Message createMessage(Message m) {
        return msgRepository.save(m);
    }

    public Message getMessageById(int id)
    {
        return msgRepository.findById(id).orElse(null);
    }
    
    public List<Message> getAllMessages()
    {
        return msgRepository.findAll();
    }

    public void deleteMessageById(int id)
    {
       msgRepository.deleteById(id);
        
    }
    public Message updateMessageTextById(int id, String str)
    {
        Message m = msgRepository.findById(id).orElse(null);
        /*if (m != null)
        {
            m.setMessageText(str);
        }*/
        if (m != null)
        {
            m.setMessageText(str);
            return msgRepository.save(m);
        }
        return msgRepository.save(m);
        
    }

    public List<Message> getAllMessagesByUser(int id)
    {
        return msgRepository.findAllMessagesByUser(id);
    }
     
}
