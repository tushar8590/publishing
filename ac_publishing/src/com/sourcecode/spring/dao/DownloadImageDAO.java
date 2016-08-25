package com.sourcecode.spring.dao;

import java.util.List;
import java.util.Map;

public interface DownloadImageDAO {

    public List<Object> getMspImageURL();
    public void insertMspImageStatus(Map<String,String> ImageMap);


}
