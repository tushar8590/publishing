package com.sourcecode.spring.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sourcecode.spring.dao.MspSpecLoaderDAO;
import com.sourcecode.spring.model.MspProductUrl;

@Service
public class MspSpecLoaderServiceImpl implements MspSpecLoaderService {

    
    @Autowired
    private MspSpecLoaderDAO mspSpecLoaderDAO;
    
    
    @Override
    public Map<String,List<MspProductUrl>> populateUrlSpecLoaderMap(List<String> sections) {
        
        Map<String,List<MspProductUrl>> sectionUrlMap = new HashMap<>();
        for(String section:sections){
            sectionUrlMap.put(section, mspSpecLoaderDAO.populateUrlSpecLoaderMap(section));
        }
         return sectionUrlMap;
    }


    @Override
    public void flushSession() {
       mspSpecLoaderDAO.flushSession();
        
    }


    @Override
    public int saveMspUrlsToBeInserted(List<MspProductUrl> toBeInserted) {
        return mspSpecLoaderDAO.saveMspUrlsToBeInserted(toBeInserted);
    }
    
}
