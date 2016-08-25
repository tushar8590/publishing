package com.sourcecode.spring.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sourcecode.spring.dao.DownloadImageDAO;

@Service
public class DownloadImagesServiceImpl implements DownloadImagesService{

	
	 @Autowired
	    private DownloadImageDAO  downloadImageDAO ;
	
	@Override
	public Map<String,String> populateImageMap() {

		Map<String,String> imageMap = new HashMap<>();
		List<Object> imageList = downloadImageDAO.getMspImageURL();

		/* imageList.forEach(s -> {
    	   imageMap.put(s.toString(), s.toString());
       });*/

		Iterator imageListIterator = imageList.iterator();

		while(imageListIterator.hasNext())
		{
			Object[] tuple = (Object[]) imageListIterator.next();
			String prodId = (String) tuple[0];
			String imageUrl = (String) tuple[1];

			imageMap.put(prodId, imageUrl);
		}
		return imageMap;
	}
	
	@Override
    public void saveDownloadedImages(List<String> sections) {
       
       Map<String,String> imageMap = new HashMap<>();
      
        //return imageMap;
    }
}
