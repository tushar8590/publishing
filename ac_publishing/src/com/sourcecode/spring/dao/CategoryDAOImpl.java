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

import com.sourcecode.spring.model.CategoryMainUrl;
import com.sourcecode.spring.model.MspProductUrl;
import com.sourcecode.spring.model.NewMenu;



@Repository
public class CategoryDAOImpl implements CategoryDAO{

    
    @Autowired
    @Qualifier("hibernate4AnnotatedSessionFactory")
    private SessionFactory sessionFactory;
    private Session session;
    
    @Override
    public List<String> getCategoryList() {
       try{
            session = this.sessionFactory.openSession();
           Query query = session.createQuery("Select distinct m.section from NewMenu m where section = 'Smart Watches' order by m.section"); 
           List<String> list = query.list();
           return list;
       }catch(HibernateException e){
           e.printStackTrace();
           return null;
       }finally{
           session.close();
       }
      
    }

    @Override
    public List<MspProductUrl> getExistingURLList(String ... sections) {
        
        try{
            session = this.sessionFactory.openSession();
            Query query = session.createQuery("from MspProductUrl m where section in (:sections)");
            query.setParameterList("sections",sections);
            
            List<MspProductUrl> list = query.list();
            return list;
            
        }catch(HibernateException e){
            e.printStackTrace();
            return null;
        }finally{
            session.close();
        } 
       
    }
    
    public  List<CategoryMainUrl> getUrlMapForSection(List<String> sections){
        try{
            session = this.sessionFactory.openSession();
            Query query = session.createQuery(" from CategoryMainUrl m where section in (:sections)");
            query.setParameterList("sections",sections);
            List<CategoryMainUrl> list = query.list();
            return list;
            
        }catch(HibernateException e){
            e.printStackTrace();
            return null;
        }finally{
            session.close();
        }  
    }

    @Override
    public int saveMspProductUrls(Set<MspProductUrl> toBeInserted) {
        try{
            session = this.sessionFactory.openSession();
            int i = 1;
            for(MspProductUrl prodUrl : toBeInserted){
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

    @Override
    public int deleteMspProductUrls(Set<MspProductUrl> toBedeleted) {
        try{
            session = this.sessionFactory.openSession();
            int i = 0;
            for(MspProductUrl prodUrl : toBedeleted){
                session.delete(prodUrl);
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
