package com.sourcecode.spring.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class DownloadImageDAOImpl implements DownloadImageDAO {
	
	
	@Autowired
    @Qualifier("hibernate4AnnotatedSessionFactory")
    private SessionFactory sessionFactory;
    private Session session;
    
	@Override
	 public List<Object> getMspImageURL(){
		
		
		 try{
	            session = this.sessionFactory.openSession();
	           Query query = session.createSQLQuery("Select distinct REPLACE(m.msp_model,CONCAT('-',SUBSTRING_INDEX(m.msp_model,'-',-1)),'') AS model, m.image_msp from msp_electronics m where m.model is not null "); 
	           List<Object> list = query.list();
	          return list;
	       }catch(HibernateException e){
	           e.printStackTrace();
	           return null;
	       }finally{
	           session.close();
	       }
		
	}
	 
	 
	 
	    public void insertMspImageStatus(Map<String,String> ImageMap){
	    	
	    }
	    
	    

}
