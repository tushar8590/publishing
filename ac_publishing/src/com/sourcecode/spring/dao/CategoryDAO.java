package com.sourcecode.spring.dao;

import java.util.List;
import java.util.Set;

import com.sourcecode.spring.model.CategoryMainUrl;
import com.sourcecode.spring.model.MspProductUrl;




public interface CategoryDAO {
    public List<String> getCategoryList();
    public List<MspProductUrl> getExistingURLList(String...section);
    public List<CategoryMainUrl> getUrlMapForSection(List<String> sections);
    public int saveMspProductUrls(Set<MspProductUrl> toBeInserted);
    public int deleteMspProductUrls(Set<MspProductUrl> toBedeleted);
}
