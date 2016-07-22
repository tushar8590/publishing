package com.sourcecode.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sourcecode.spring.dao.UserDAO;



@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;
    
    @Override
    public boolean isValidUser(String userName) {
       
        return userDAO.isValidUser(userName);
    }
    
}
