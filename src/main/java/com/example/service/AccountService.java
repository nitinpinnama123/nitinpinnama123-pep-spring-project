package com.example.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.entity.Account;

import Util.ConnectionUtil;

public class AccountService {
    private static AccountService accountService = null;
    private List<Account> accounts = new ArrayList<Account>();

    public AccountService() {

    }

    public static AccountService instance() {
        if (accountService == null)
        {
            accountService = new AccountService();
        }
        return accountService;
    }

    @PostMapping("/register")
    public @ResponseBody Account registerUser(@RequestParam String username, @RequestParam String password) throws SQLException {
        String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int acct_id = -1;
        Account a = null;
        try {
            conn = ConnectionUtil.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.execute();

            rs = stmt.getGeneratedKeys();
            while ( rs.next() ) {
                acct_id = rs.getInt(1);
                System.out.println("Account created with id  = " + acct_id);
            }

            conn.commit();
            
            a = getAccountById(acct_id);

       } catch (SQLException e) {
            throw e;
       }
       finally {
        if (rs != null) {rs.close();}
        if (stmt != null) { stmt.close(); }
       }
       return a;
    }

    @GetMapping("/account/{username}")
    public @ResponseBody Account getAccountByUsername(@PathVariable String username) throws SQLException
    {
        Account accountToGet = null;
        String sql = "SELECT accountId, username, password FROM account WHERE username = ?";
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;

        try {
            System.out.println("Getting account with username = " + username);
            conn = ConnectionUtil.getConnection();

            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, username);

            rs = pStmt.executeQuery();
            
            while (rs.next())
            {
                accountToGet = new Account();
                accountToGet.setAccountId(rs.getInt("accountId"));
                accountToGet.setUsername(rs.getString("username"));
                accountToGet.setPassword(rs.getString("password"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            if (rs != null) rs.close();
            if (pStmt != null) pStmt.close();
        }
        if (accountToGet != null) {
            System.out.println("Got account = " + accountToGet);
        } else {
            System.out.println("No account with username = " + username);
        }
        return accountToGet;
    }


    @PostMapping("/login")
    public @ResponseBody Boolean loginUser(@RequestParam String username, @RequestParam String password) throws SQLException
    {
        ResultSet resultSet = null;
        PreparedStatement pStmt = null;
        ResultSet rsKeys = null;
        int acct_id = -1;
        boolean retVal = false;
        try {
            String sql = "SELECT * from account WHERE username = ? AND password = ?";
            Connection conn = ConnectionUtil.getConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, username);
            pStmt.setString(2, password);
            resultSet = pStmt.executeQuery();

            if(resultSet.next())
            {
                System.out.println("Welcome. ");
                retVal = true;
            }
            else {
                System.out.println("Invalid username and password");
                retVal = false;
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (resultSet != null)
            {
                resultSet.close();
            }
            if (pStmt != null)
            {
                pStmt.close();
            }
        }
        return retVal;
    }

    @GetMapping("/account/{accountId}")
    public @ResponseBody Account getAccountById(@PathVariable int account_id) throws SQLException {
        Account accountToGet = null;
        String sql = "SELECT accountId, username, password FROM account WHERE accountId = ?";
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;

        try {
            System.out.println("Getting account with ID = " + account_id);
            conn = ConnectionUtil.getConnection();

            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, account_id);

            rs = pStmt.executeQuery();
            
            while (rs.next())
            {
                accountToGet = new Account();
                accountToGet.setAccountId(rs.getInt("accountId"));
                accountToGet.setUsername(rs.getString("username"));
                accountToGet.setPassword(rs.getString("password"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            if (rs != null) rs.close();
            if (pStmt != null) pStmt.close();
        }
        System.out.println("Got account = " + accountToGet);
        return accountToGet;   
    }


}
