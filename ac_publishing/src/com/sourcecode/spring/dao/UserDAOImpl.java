package com.sourcecode.spring.dao;



import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.sourcecode.spring.model.User;



@Repository
public class UserDAOImpl implements UserDAO {

    
    @Autowired
    @Qualifier("hibernate4AnnotatedSessionFactory")
    private SessionFactory sessionFactory;
   
    @Override
    public boolean isValidUser(String userName) {
      // Session session = this.sessionFactory.getCurrentSession();
        Session session = this.sessionFactory.openSession();
         Query query =  session.createQuery("from User u where u.userName=:username");
         query.setString("username", userName);
        User user  = (User)query.uniqueResult();
        session.close();
        if(user != null)
            return true;
        else
            return false;
      
        
        
    }
    
}
