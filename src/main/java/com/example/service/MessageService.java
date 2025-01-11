package com.example.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public @ResponseBody Message getMessageByUser(int userID) throws SQLException
    {
        Message messageToGet = null;
        String sql = "SELECT * FROM message WHERE posted_by = ?";
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;

        try {
            System.out.println("Getting message with user = " + userID);
            conn = ConnectionUtil.getConnection();

            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, userID);

            rs = pStmt.executeQuery();
            
            while (rs.next())
            {
                messageToGet = new Message();
                messageToGet.setMessageId(rs.getInt("message_id"));
                messageToGet.setPostedBy(rs.getInt("posted_by"));
                messageToGet.setMessageText(rs.getString("message_text"));
                messageToGet.setTimePostedEpoch(rs.getLong("time_posted_epoch"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            if (rs != null) rs.close();
            if (pStmt != null) pStmt.close();
        }
        if (messageToGet != null) {
            System.out.println("Got message = " + messageToGet);
        } else {
            System.out.println("No message with user = " + userID);
        }
        return messageToGet;
    }


    public Message createMessage(int postedBy, String msg_text, long time_posted_epoch) throws SQLException {
        String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
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

    public Message getMessageById(int id) throws SQLException
    {
        Message messageToGet = null;;
        ResultSet resultSet = null;
        PreparedStatement pStmt = null;
        String sql = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message WHERE message_id = ?";

        try {
            Connection conn = ConnectionUtil.getConnection();

            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, id);

            resultSet = pStmt.executeQuery();
            
            while (resultSet.next())
            {
                messageToGet = new Message();
                messageToGet.setMessageId(resultSet.getInt("message_id"));
                messageToGet.setPostedBy(resultSet.getInt("posted_by"));
                messageToGet.setMessageText(resultSet.getString("message_text"));
                messageToGet.setTime_posted_epoch(resultSet.getLong("time_posted_epoch"));
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
    
    public List<Message> getAllMessages() throws SQLException
    {
        List<Message> messages = new ArrayList<Message>();
        Message messageFetched = null;
        String sql = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message";
        ResultSet resultSet = null;
        Statement stmt = null;
        try {
            Connection conn = ConnectionUtil.getConnection();

            stmt = conn.createStatement();

            resultSet = stmt.executeQuery(sql);

            while (resultSet.next())
            {
                messageFetched = new Message();
                messageFetched.setMessageId(resultSet.getInt("message_id"));
                messageFetched.setPostedBy(resultSet.getInt("posted_by"));
                messageFetched.setMessageText(resultSet.getString("message_text"));
                messageFetched.setTime_posted_epoch(resultSet.getLong("time_posted_epoch"));
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

    public Message deleteMessageById(int id) throws SQLException
    {
        PreparedStatement pStmt = null;
        String sql = "DELETE FROM message WHERE message_id = ?";
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
    
    public Message updateMessageTextById(int id, String str) throws SQLException
    {
 
        PreparedStatement pStmt = null;
        String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
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

    public List<Message> getAllMessagesByUser(int userID) throws SQLException
    {
        List<Message> messagesByUser = new ArrayList<Message>();
        
        Message messageFetched = null;
        PreparedStatement pStmt = null;
        ResultSet resultSet = null;
        String sql = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message WHERE posted_by = ?";

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
                messageFetched.setMessageId(resultSet.getInt("message_id"));
                messageFetched.setPostedBy(resultSet.getInt("posted_by"));
                messageFetched.setMessageText(resultSet.getString("message_text"));
                messageFetched.setTime_posted_epoch(resultSet.getLong("time_posted_epoch"));
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
