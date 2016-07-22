package com.sourcecode.spring.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.sourcecode.spring.model.MspElectronics;
import com.sourcecode.spring.model.MspProductUrl;

@Repository
public class MspCatDataDAOImpl implements MspCatDataDAO{

    @Autowired
    @Qualifier("hibernate4AnnotatedSessionFactory")
    private SessionFactory sessionFactory;
    private Session session;
   
    
    @Override
    public List<MspProductUrl> getmspUrlsWithInsertedFlag(String section) {
        try{
            session = this.sessionFactory.openSession();
            Query query = session.createQuery("from MspProductUrl m where m.status = 'i' and  section = ?"); 
            query.setParameter(0,section);
            List<MspProductUrl> list = query.list();
            return list;
       }catch(HibernateException e){
           e.printStackTrace();
           return null;
       }finally{
           session.close();
       }
      
      
    }


    @Override
    public synchronized int saveMspUrlsToBeInserted(Set<MspElectronics> toBeInserted) {
       
        try{
            session = this.sessionFactory.openSession();
            int i = 1;
            for(MspElectronics prodUrl : toBeInserted){
                session.save(prodUrl);
                i++;
            }
            return i;
        }catch(HibernateException e){
            e.printStackTrace();
            return 0;
        }finally{
            session.close();
        } 
    }
    
}
