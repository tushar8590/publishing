package com.sourcecode.spring.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sourcecode.spring.model.MspElectronics;
import com.sourcecode.spring.model.MspProductUrl;

public interface MspCatDataService {
    public Map<String,List<MspProductUrl>> getMspUrlsWithInsertedFlag(List<String>  sections);
    public int saveMspUrlsToBeInserted(Set<MspElectronics> toBeInserted);
}
