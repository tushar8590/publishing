package com.sourcecode.spring.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sourcecode.spring.dao.CategoryDAO;
import com.sourcecode.spring.model.CategoryMainUrl;
import com.sourcecode.spring.model.MspProductUrl;


@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryDAO categoryDAO;
    
    @Override
    public Map<String, String> getCategoryList() {
        Map<String, String> sectionMap = new HashMap<>();
        List<String> menuList = categoryDAO.getCategoryList();
        
        menuList.forEach(s -> {
            sectionMap.put(s, s);
        });
        
        return sectionMap;
    }
    
    @Override
    public List<MspProductUrl> getExistingURLList(String... section) {
        return categoryDAO.getExistingURLList(section);
        
    }
    
    public Map<String, List<String>> getUrlMapForSection(List<String> sections) {
        List<CategoryMainUrl> list = categoryDAO.getUrlMapForSection(sections);
        Map<String, List<String>> urlMap = new HashMap<>();
        
        list.forEach(cat -> {
            
            urlMap.put(cat.getSection(), Arrays.asList(cat.getFirstPageUrl(), cat.getSecondPageUrl(), Integer.toString(cat.getTotalPages())));
        });
        
        return urlMap;
    }

    @Override
    public int saveMspProductUrls(Set<MspProductUrl> toBeInserted) {
        return categoryDAO.saveMspProductUrls(toBeInserted);
    }

    @Override
    public int deleteMspProductUrls(Set<MspProductUrl> toBedeleted) {
        return categoryDAO.deleteMspProductUrls(toBedeleted);
    }
}
