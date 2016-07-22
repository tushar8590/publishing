package com.sourcecode.spring.dao;

import java.util.List;

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
public class MspSpecLoaderDAOImpl implements MspSpecLoaderDAO{

    @Autowired
    @Qualifier("hibernate4AnnotatedSessionFactory")
    private SessionFactory sessionFactory;
    private Session session;
    
    @Override
    public List<MspProductUrl> populateUrlSpecLoaderMap(String categories) {
        try{
            session = this.sessionFactory.openSession();
            Query query = session.createQuery("from MspProductUrl m where section in (:sections) and product_spec is null");
            query.setParameter("sections",categories);
            
            List<MspProductUrl> list = query.list();
            System.out.println(list.size());
            return list;
            
        }catch(HibernateException e){
            e.printStackTrace();
            return null;
        }finally{
            session.close();
        } 
       
      
    }

    @Override
    public void flushSession() {
        session.flush();
        
    }

    @Override
    public synchronized int saveMspUrlsToBeInserted(List<MspProductUrl> toBeInserted) {
        try{
            session = this.sessionFactory.openSession();
            int i = 1;
            for(MspProductUrl prodUrl : toBeInserted){
                session.update(prodUrl);
                session.flush();
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
