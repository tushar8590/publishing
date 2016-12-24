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
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlHeading1;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.sourcecode.spring.dao.MspCatDataDAOImpl;
import com.sourcecode.spring.job.MSPSpecLoader.DataExtractor;
import com.sourcecode.spring.model.MspElectronics;
import com.sourcecode.spring.model.MspProductUrl;
import com.sourcecode.spring.service.MspCatDataService;
import com.sourcecode.standalone.FillMspElectronicsColumns;

@Component
public class MSPCatDataExtractor {
	//private static Logger logger = Logger.getLogger("com.gargoylesoftware");
	
    static {
    	java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    	
    }
    
    static String idBase = "ACNEW";
    int count;
    
    @Autowired
    private MspCatDataService catDataService;
    private Map<String, List<MspProductUrl>> mspProdUrlsToBeInsertedMap;
    static int breakFactor = 1;
    private Set<MspElectronics> masterSetToBeInserted;
    
    public void getMspUrlsWithInsertedFlag(List<String> sections) {
        
        mspProdUrlsToBeInsertedMap = catDataService.getMspUrlsWithInsertedFlag(sections);
        System.out.println("Total Number of Products to be inserted " + mspProdUrlsToBeInsertedMap.get(sections.get(0)).size());
    }
    
    @Async
    public void processData(List<String> sections) {
        System.out.println("Starting at " + System.currentTimeMillis());
        getMspUrlsWithInsertedFlag(sections);
       int size =  processInsertedUrlMap();
       System.out.println(size);
        FillMspElectronicsColumns.execute(sections);        
        System.out.println("Ending at " + System.currentTimeMillis());
        
    }
    
    public int processInsertedUrlMap() {
        System.out.println("Starting Master Process");
        ExecutorService executor = Executors.newCachedThreadPool();
        Set<Future<Set<MspElectronics>>> futureSet = new HashSet<Future<Set<MspElectronics>>>();
        mspProdUrlsToBeInsertedMap.forEach((k, v) -> {
            // subdivide the arraylist corresponding to each section k
            int a = 0;
            int b = v.size() / breakFactor;
            
            for (int i = 0; i < breakFactor; i++) {
               // List<MspProductUrl> listn = v.subList(a, b);
            	 List<MspProductUrl> listn = new  ArrayList<>();
            	 listn.addAll(v.subList(a, b));
                a = b;
                b = b + v.size() / breakFactor;
                
                Callable<Set<MspElectronics>> callable = this.new DataExtractor(listn, k, idBase);
                Future<Set<MspElectronics>> future = executor.submit(callable);
                futureSet.add(future);
            }
            if(breakFactor > 1){
            	List<MspProductUrl> listn = v.subList(b, v.size()); // remaining all the products
            	Callable<Set<MspElectronics>> callable = this.new DataExtractor(listn,k, idBase);
            	Future<Set<MspElectronics>> future = executor.submit(callable);
                futureSet.add(future);
            }
            
            
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
        return futureSet.size();
    }
    
    class DataExtractor implements Callable {
    	
    	
        String query;
        List<String> params;
        
        List<MspProductUrl> url;
        String section;
       // HtmlUnitDriver driver;
        WebClient webClient;
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
            //driver = new HtmlUnitDriver(BrowserVersion.INTERNET_EXPLORER_8);
            webClient = new WebClient(BrowserVersion.CHROME);
           // webClient = new WebClient(BrowserVersion.FIREFOX_24);

            webClient.getOptions().setCssEnabled(false);//if you don't need css
            webClient.getOptions().setJavaScriptEnabled(false);//if you don't need js
            
        }
        
        @Override
        public Set<MspElectronics> call() throws Exception {
            
            processSet = new HashSet<>();
            
            Iterator<MspProductUrl> itr = url.iterator();
            MspProductUrl currentUrl = null;
            
            System.out.println("New Thread " + Thread.currentThread().getName()+" with size "+this.url.size());
            while (itr.hasNext()) {
                
                currentUrl = itr.next();
               System.out.println(currentUrl.getUrl());
              //  driver.get(currentUrl.getUrl());
                HtmlPage page = webClient.getPage(currentUrl.getUrl());
                try {
                    
                    image = ((HtmlImage)page.getByXPath("//*[@class='prdct-dtl__img']").get(0)).getAttribute("src");
                    
                    //getting model
                    
                    model = ((HtmlHeading1)page.getByXPath("//*[@class='prdct-dtl__ttl']").get(0)).asText(); 
                    
                }
                catch (Exception e) {
                    System.out.println("Inside Exception");
                    
                    e.printStackTrace();
                    continue;
                }
                
               // List<HtmlImage> listLogo = (List<HtmlImage>) page.getByXPath("//img[contains(@class,'prc-grid__logo')]");
                List<HtmlDivision> listLogo = (List<HtmlDivision>) page.getByXPath("//div[contains(@class,'prc-grid__logo')]");
               // System.out.println("listLogo  "+listLogo.size());
                // fetch for each vendor of the product
                for (int i = 1; i <= listLogo.size(); i++) {
                    try {
                        MspElectronics obj = new MspElectronics();
                        obj.setImageMsp(image);
                        obj.setProductId(currentUrl.getProductId());
                        obj.setSection(section);
                        obj.setModel(model);
                        obj.setMspModel(currentUrl.getModel());
                        // getting vendor url
                        HtmlDivision elem = (HtmlDivision) page.getByXPath("/html/body/div[4]/div[4]/div[1]/div[1]/div[1]/div[" + i + "]/div[1]/div[4]/div").get(0);
                        obj.setUrl(elem.getAttribute("data-url"));
                                                
                        //getting price
                        HtmlSpan span = (HtmlSpan) page.getByXPath("/html/body/div[4]/div[4]/div[1]/div[1]/div[1]/div[" + i + "]/div[1]/div[3]/div[1]/span").get(0);
                        obj.setPrice(span.asText().replaceAll("[^0-9.]", ""));
                        
                        processSet.add(obj);
                    }
                    catch (Exception e) {
                    	e.printStackTrace();
                        //System.out.println(e.getMessage());
                        //continue;
                        
                    }
                }
               // System.out.println(url);

                //currentUrl.setStatus("D");
                catDataService.saveMspUrlsToBeInserted(processSet);
                processSet.clear();
            }
         //   System.out.println("Going to insert Master Set for Thread " + Thread.currentThread().getName()+" with records "+processSet.size());
          //  catDataService.saveMspUrlsToBeInserted(processSet);

            //driver.quit();
            
            System.out.println("Ending loop");
            //webClient.closeAllWindows();
            return processSet;
        }
        
    }
    
}
