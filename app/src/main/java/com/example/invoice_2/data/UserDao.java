package com.example.invoice_2.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE username = :username")
    User findByUsername(String username);

    @Query("SELECT COUNT(*) > 0 FROM users WHERE username = :username")
    boolean exists(String username);

    @Query("SELECT COUNT(*) > 0 FROM users WHERE username = :username AND password = :password")
    boolean checkUser(String username, String password);
} 