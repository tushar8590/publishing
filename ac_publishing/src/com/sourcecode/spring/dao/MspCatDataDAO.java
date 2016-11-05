package com.sourcecode.spring.dao;
import java.util.List;
import java.util.Set;

import com.sourcecode.spring.model.MspElectronics;
import com.sourcecode.spring.model.MspProductUrl;
import com.sourcecode.spring.model.MspProductUrl;
public interface MspCatDataDAO {
    public List<MspProductUrl> getmspUrlsWithInsertedFlag(String sections);
    public int saveMspUrlsToBeInserted(Set<MspElectronics> toBeInserted);
   // public void updateMspProdutUrlStatus(MspProductUrl productUrl);

}
