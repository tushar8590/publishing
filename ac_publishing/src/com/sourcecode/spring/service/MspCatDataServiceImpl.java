package com.sourcecode.spring.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sourcecode.spring.dao.MspCatDataDAO;
import com.sourcecode.spring.model.MspElectronics;
import com.sourcecode.spring.model.MspProductUrl;



@Service
public class MspCatDataServiceImpl implements MspCatDataService {

    
    @Autowired
    private MspCatDataDAO mspCatDataDAO;
    
    @Override
    public Map<String,List<MspProductUrl>> getMspUrlsWithInsertedFlag(List<String>  sections) {
       
       Map<String,List<MspProductUrl>> sectionUrlMap = new HashMap<>();
       for(String section:sections){
           sectionUrlMap.put(section, mspCatDataDAO.getmspUrlsWithInsertedFlag(section));
       }
        return sectionUrlMap;
    }

    @Override
    public int saveMspUrlsToBeInserted(Set<MspElectronics> toBeInserted) {
        
        return mspCatDataDAO.saveMspUrlsToBeInserted(toBeInserted);
    }

	
    
    
    
        }
