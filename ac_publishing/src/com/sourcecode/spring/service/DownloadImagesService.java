package com.sourcecode.spring.service;

import java.util.List;
import java.util.Map;


public interface DownloadImagesService {
	 public Map<String,String> populateImageMap();
	 public void saveDownloadedImages(List<String>  sections);
}
