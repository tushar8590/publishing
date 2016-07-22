package com.sourcecode.spring.dao;

import java.util.List;
import com.sourcecode.spring.model.MspProductUrl;

public interface MspSpecLoaderDAO {
    public  List<MspProductUrl>populateUrlSpecLoaderMap(String categories);
    public void flushSession();
    public int saveMspUrlsToBeInserted(List<MspProductUrl> toBeInserted);
}
