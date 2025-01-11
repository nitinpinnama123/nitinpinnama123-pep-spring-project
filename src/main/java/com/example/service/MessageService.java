package com.example.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.entity.Message;

import Util.ConnectionUtil;

public class MessageService {
    private static MessageService msgService = null;

    // private List<Message> messages = new ArrayList<Message>();

    public static MessageService instance() {
        if (msgService == null)
        {
            msgService = new MessageService();
        }
        return msgService;
    }

    @PostMapping("/messages")
    public @ResponseBody Message createMessage(@RequestParam int postedBy, @RequestParam String msg_text, @RequestParam long time_posted_epoch) throws SQLException {
        String sql = "INSERT INTO message (postedBy, messageText, timePostedEpoch) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        int msg_id = -1;
        Message m = null;
        try {
            conn = ConnectionUtil.getConnection();
            statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //statement.setInt(1, messageID);
            statement.setInt(1, postedBy);
            statement.setString(2, msg_text);
            statement.setLong(3, time_posted_epoch);
            statement.execute();

            rs = statement.getGeneratedKeys();
            while(rs.next())
            {
                msg_id = rs.getInt(1);
                System.out.println("Message created with id = " + msg_id);
            }
           
            conn.commit();

            m = getMessageById(msg_id);

        } catch(SQLException e)
        {
            throw e;
        }
        finally {
            if (rs != null) {rs.close();}
            if (statement != null)
            {
                statement.close();
            }
        }
        return m;
    }

    @GetMapping("/messages/{messageId}")
    public Message getMessageById(int id) throws SQLException
    {
        Message messageToGet = null;;
        ResultSet resultSet = null;
        PreparedStatement pStmt = null;
        String sql = "SELECT messageId, postedBy, messageText, timePostedEpoch FROM message WHERE messageId = ?";

        try {
            Connection conn = ConnectionUtil.getConnection();

            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, id);

            resultSet = pStmt.executeQuery();
            
            while (resultSet.next())
            {
                messageToGet = new Message();
                messageToGet.setMessageId(resultSet.getInt("messageId"));
                messageToGet.setPostedBy(resultSet.getInt("postedBy"));
                messageToGet.setMessageText(resultSet.getString("messageText"));
                messageToGet.setTimePostedEpoch(resultSet.getLong("timePostedEpoch"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (resultSet != null) { resultSet.close(); }
            if (pStmt != null) {
                pStmt.close();
            }
        }

        return messageToGet;
    }
    
    @GetMapping("/messages")
    public @ResponseBody List<Message> getAllMessages() throws SQLException
    {
        List<Message> messages = new ArrayList<Message>();
        Message messageFetched = null;
        String sql = "SELECT messageId, postedBy, messageText, timePostedEpoch FROM message";
        ResultSet resultSet = null;
        Statement stmt = null;
        try {
            Connection conn = ConnectionUtil.getConnection();

            stmt = conn.createStatement();

            resultSet = stmt.executeQuery(sql);

            while (resultSet.next())
            {
                messageFetched = new Message();
                messageFetched.setMessageId(resultSet.getInt("messageId"));
                messageFetched.setPostedBy(resultSet.getInt("postedBy"));
                messageFetched.setMessageText(resultSet.getString("messageText"));
                messageFetched.setTimePostedEpoch(resultSet.getLong("timePostedEpoch"));
                messages.add(messageFetched);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            if (resultSet != null) resultSet.close();
            if (stmt != null) stmt.close();
        }

        return messages;
    }

    @DeleteMapping("/messages/{messageId}")
    public Message deleteMessageById(int id) throws SQLException
    {
        PreparedStatement pStmt = null;
        String sql = "DELETE FROM message WHERE messageId = ?";
        Message mRet = null;

        try {

            Connection conn = ConnectionUtil.getConnection();

            mRet = getMessageById(id);

            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, id);

            pStmt.executeUpdate();
            conn.commit();

        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            if (pStmt != null) {
                pStmt.close();
            }
        }
        return mRet;
    }
    
    @PatchMapping("/messages/{messageId}")
    public Message updateMessageTextById(int id, String str) throws SQLException
    {
 
        PreparedStatement pStmt = null;
        String sql = "UPDATE message SET messageText = ? WHERE messageId = ?";
        Message mReturn = null;
        try {
            Connection conn = ConnectionUtil.getConnection();

            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, str);
            pStmt.setInt(2, id);
            
            pStmt.executeUpdate();
            conn.commit();
            mReturn = getMessageById(id);

        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            if (pStmt != null) pStmt.close();
        }
        return mReturn;
        
    }

    @GetMapping("/accounts/{accountId}/messages")
    public List<Message> getAllMessagesByUser(int userID) throws SQLException
    {
        List<Message> messagesByUser = new ArrayList<Message>();
        
        Message messageFetched = null;
        PreparedStatement pStmt = null;
        ResultSet resultSet = null;
        String sql = "SELECT messageId, postedBy, messageText, timePostedEpoch FROM message WHERE postedBy = ?";

        /*return messages.stream()
        .filter(m -> m.posted_by == userID)
        .collect(Collectors.toList());*/
        try {
            Connection conn = ConnectionUtil.getConnection();

            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, userID);
            resultSet = pStmt.executeQuery();

            while(resultSet.next())
            {
                messageFetched = new Message();
                messageFetched.setMessageId(resultSet.getInt("messageId"));
                messageFetched.setPostedBy(resultSet.getInt("postedBy"));
                messageFetched.setMessageText(resultSet.getString("messageText"));
                messageFetched.setTimePostedEpoch(resultSet.getLong("timePostedEpoch"));
                messagesByUser.add(messageFetched);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            if (resultSet != null) resultSet.close();
            if (pStmt != null) pStmt.close();
        }
        
        return messagesByUser;
    }
}
