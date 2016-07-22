package com.sourcecode.spring.service;

import java.util.List;
import java.util.Map;

import com.sourcecode.spring.model.MspProductUrl;

public interface MspSpecLoaderService {
    public Map<String,List<MspProductUrl>> populateUrlSpecLoaderMap(List<String> categories);
    public void flushSession();
    public int saveMspUrlsToBeInserted(List<MspProductUrl> toBeInserted);
}
