package com.sourcecode.spring.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sourcecode.spring.model.CategoryMainUrl;
import com.sourcecode.spring.model.MspProductUrl;
import com.sourcecode.spring.model.NewMenu;

public interface CategoryService {
        public Map<String,String> getCategoryList(); 
        public List<MspProductUrl> getExistingURLList(String...section);
        public Map<String,List<String>> getUrlMapForSection(List<String> sections);
        public int saveMspProductUrls(Set<MspProductUrl> toBeInserted);
        public int deleteMspProductUrls(Set<MspProductUrl> toBedeleted);
}
