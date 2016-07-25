package com.sourcecode.spring.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.sourcecode.spring.dao.MspCatDataDAOImpl;
import com.sourcecode.spring.model.MspElectronics;
import com.sourcecode.spring.model.MspProductUrl;
import com.sourcecode.spring.service.MspCatDataService;
import com.sourcecode.standalone.FillMspElectronicsColumns;

@Component
public class MSPCatDataExtractor {
	//private static Logger logger = Logger.getLogger("com.gargoylesoftware");
    static {
    	
    	
    }
    
    static String idBase = "ACNEW";
    int count;
    
    @Autowired
    private MspCatDataService catDataService;
    private Map<String, List<MspProductUrl>> mspProdUrlsToBeInsertedMap;
    static int breakFactor = 12;
    private Set<MspElectronics> masterSetToBeInserted;
    
    public void getMspUrlsWithInsertedFlag(List<String> sections) {
        
        mspProdUrlsToBeInsertedMap = catDataService.getMspUrlsWithInsertedFlag(sections);
        System.out.println("Total Number of Products to be inserted " + mspProdUrlsToBeInsertedMap.get(sections.get(0)).size());
    }
    
    @Async
    public void processData(List<String> sections) {
        System.out.println("Starting at " + System.currentTimeMillis());
        getMspUrlsWithInsertedFlag(sections);
        processInsertedUrlMap();
        FillMspElectronicsColumns.execute(sections);        
        System.out.println("Ending at " + System.currentTimeMillis());
        
    }
    
    public void processInsertedUrlMap() {
        System.out.println("Starting Master Process");
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Set<Future<Set<MspElectronics>>> futureSet = new HashSet<Future<Set<MspElectronics>>>();
        mspProdUrlsToBeInsertedMap.forEach((k, v) -> {
            // subdivide the arraylist corresponding to each section k
            int a = 0;
            int b = v.size() / breakFactor;
            
            for (int i = 0; i < breakFactor; i++) {
                List<MspProductUrl> listn = v.subList(a, b);
                
                a = b;
                b = b + v.size() / breakFactor;
                
                Callable<Set<MspElectronics>> callable = this.new DataExtractor(listn, k, idBase);
                Future<Set<MspElectronics>> future = executor.submit(callable);
                futureSet.add(future);
            }
            
            List<MspProductUrl> listn = v.subList(a, v.size());
            
            
             Callable<Set<MspElectronics>> callable = this.new DataExtractor(listn,k, idBase);
              Future<Set<MspElectronics>> future = executor.submit(callable);
              futureSet.add(future);
             
            
        });
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.out.println(e);
        }
        System.out.println("Finished");
        /*
         * int i = 0;
         * for(Future<Set<MspElectronics>> f: futureSet){
         * System.out.println(++i);
         * try {
         * masterSetToBeInserted.addAll(f.get());
         * }
         * catch (InterruptedException | ExecutionException e) {
         * e.printStackTrace();
         * }
         * }
         * 
         * System.out.println("Going to insert Master Set");
         * catDataService.saveMspUrlsToBeInserted(masterSetToBeInserted);
         */
    }
    
    class DataExtractor implements Callable {
    	
    	
        String query;
        List<String> params;
        
        List<MspProductUrl> url;
        String section;
        HtmlUnitDriver driver;
        String vendorUrl;
        String deliveryTime;
        String emi;
        String cod;
        String rating;
        String image;
        String price;
        String productid;
        String model;
        Set<MspElectronics> processSet;
        
        public DataExtractor(List<MspProductUrl> baseUrl, String section, String id) {
            this.url = baseUrl;
            this.section = section;
            this.productid = id;
            params = new ArrayList<>();
            driver = new HtmlUnitDriver(BrowserVersion.INTERNET_EXPLORER_11);
        }
        
        @Override
        public Set<MspElectronics> call() throws Exception {
            
            processSet = new HashSet<>();
            
            Iterator<MspProductUrl> itr = url.iterator();
            MspProductUrl currentUrl = null;
            
            System.out.println("New Thread " + Thread.currentThread().getName());
            while (itr.hasNext()) {
                
                currentUrl = itr.next();
                
                driver.get(currentUrl.getUrl());
                
                try {
                    
                    if (driver.findElements(
                        By.xpath("/html/body/div[4]/div[2]/div/div[1]/div[2]/img")).size() != 0) {
                        image = driver.findElement(
                            By.xpath("/html/body/div[4]/div[2]/div/div[1]/div[2]/img")).getAttribute(
                            "src");
                    }
                    else if (driver.findElements(By.xpath("//*[@id='mspSingleImg']")).size() != 0) {
                        image = driver.findElement(
                            By.xpath("//*[@id='mspSingleImg']")).getAttribute(
                            "src");
                        
                    }
                    
                    //getting model
                    List<WebElement> listTh = driver.findElementsByXPath("//h1[contains(@class,'prdct-dtl__ttl')]");
                    model = listTh.get(0).getText();
                    
                }
                catch (Exception e) {
                    System.out.println("Inside Exception");
                    
                    e.printStackTrace();
                    continue;
                }
                
                List<WebElement> listLogo = driver.findElementsByXPath("//img[contains(@class,'prc-grid__logo')]");
                // fetch for each vendor of the product
                for (int i = 1; i <= listLogo.size(); i++) {
                    try {
                        MspElectronics obj = new MspElectronics();
                        obj.setImageMsp(image);
                        obj.setProductId(currentUrl.getProductId());
                        obj.setSection(section);
                        obj.setModel(model);
                        // getting vendor url
                        WebElement elem = driver.findElement(By.xpath("/html/body/div[4]/div[4]/div[1]/div[1]/div[1]/div[" + i + "]/div[1]/div[4]/div"));
                        obj.setUrl(elem.getAttribute("data-url"));
                                                
                        //getting price
                        elem = driver.findElement(By.xpath("/html/body/div[4]/div[4]/div[1]/div[1]/div[1]/div[" + i + "]/div[1]/div[3]/div[1]/span"));
                        obj.setPrice(elem.getText().replaceAll("[^0-9.]", ""));
                        
                        
                        processSet.add(obj);
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                        continue;
                        
                    }
                }
                
            }
            System.out.println("Going to insert Master Set for Thread " + Thread.currentThread().getName());
            catDataService.saveMspUrlsToBeInserted(processSet);
            driver.quit();
            return processSet;
        }
        
    }
    
}
