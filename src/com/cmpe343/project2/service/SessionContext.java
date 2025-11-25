package com.cmpe343.project2.service;

import com.cmpe343.project2.model.User;

/**
 * Manages the current user session.
 * Allows different parts of the app to know who is logged in.
 */
public class SessionContext {
    private static SessionContext instance;
    private User currentUser;

    private SessionContext() {
    }

    public static SessionContext getInstance() {
        if (instance == null) {
            instance = new SessionContext();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}