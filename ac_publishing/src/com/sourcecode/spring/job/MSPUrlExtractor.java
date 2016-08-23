package com.sourcecode.spring.job;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sourcecode.spring.model.MspProductUrl;
import com.sourcecode.spring.service.CategoryService;
import com.sourcecode.standalone.FetchMaxPageNumberForCategory;
import com.sourcecode.standalone.FillMSPProductURlColumns;


@Component
public class MSPUrlExtractor {

    
    static {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }
    
    // static Map<String, List<String>> urlMap;
    static boolean isRunning = false;
    
    // new code as per the spring - mvc and Hib design
    
    /*
     * 
     * 1. get the Data from existing table using entity MspProductUrl and CAtegoryMainUrl
     * 2. Create a set s_local contains all the urls for section s1....sn in local database
     * 3. Create a set s_msp contains all the urls for section s1....sn in msp website
     * 4. create the set s_msp_temp that is the replica of the set s_msp
     * 5. s_msp.removeAll(s_local); such that s_msp contains all the URLs to be added in database
     * 6. s_local.removeAll(s_msp_temp); such that s_local contains all the URLs to be deleted from the db
     */
    
    private Set<MspProductUrl> s_msp;
   
    private Set<MspProductUrl> s_local; // data fron local db
    
    private Map<String, List<String>> urlMap;
    
    @Autowired
    private CategoryService categoryService;
    
    @Async
    public void processData(List<String> sections) {
        // running FetchMaxPageNumber
        
        FetchMaxPageNumberForCategory.execute(sections);
        prepareDatabaseListOfUrls(sections);
        prepareURLMap(sections);
        processURLMap();
    }
    
    private void prepareDatabaseListOfUrls(List<String> sections) {
        for (String section : sections)
            s_local = new LinkedHashSet(categoryService.getExistingURLList(section));
        
    }
    
    private void prepareURLMap(List<String> sections) {
        urlMap = categoryService.getUrlMapForSection(sections);
        System.out.println(urlMap);
    }
    
    private void processURLMap() {
      
        
        
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        s_msp = new LinkedHashSet<>();
        
        Set<Future<Set<MspProductUrl>>> futureSet = new HashSet<Future<Set<MspProductUrl>>>();
        urlMap.forEach((k, v) -> {
            Callable<Set<MspProductUrl>> callable = this.new DataExtractor(v.get(0), v.get(1), v.get(2), k);
            Future<Set<MspProductUrl>> future = executor.submit(callable);
            futureSet.add(future);
         });
        executor.shutdown();
        
        // to wait for the executor to complete
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.out.println(e);
        }
        
        
        // collect the output from all the Futures
        int i  = 0;
        for(Future<Set<MspProductUrl>> f: futureSet){
            System.out.println(++i);
            try {
                s_msp.addAll(f.get());
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    /*    futureSet.forEach(s ->{
            try {
                s_msp.addAll(s.get());
                System.out.println(++i);
            }
            catch (Exception e) {
                 e.printStackTrace();
            }
            
        });*/
     
        System.out.println("All elememnts from msp "  + s_msp.size());
        System.out.println("Local Elements "+s_local.size());
        
        Set<MspProductUrl> s_msp_temp = new LinkedHashSet<>();
        s_msp_temp.addAll(s_msp);
        
        // to be addded
        s_msp.removeAll(s_local);
        System.out.println(s_msp.size());
        
        // to be removed
        s_local.removeAll(s_msp_temp);
        System.out.println(s_local.size());
        
        System.out.println("Total Inserted " + categoryService.saveMspProductUrls(s_msp));
        System.out.println("Total Deleted " + categoryService.deleteMspProductUrls(s_local));
        
        FillMSPProductURlColumns.execute();
        
    }
    
    class DataExtractor implements Callable {
        String baseUrl;
        String otherUrls;
        int limit;
        String productUrl;
        WebClient webClient;
        String section;
        String keyword; // 2.html
        String query;
        List<String> params;
        
       
        
        public DataExtractor(String baseUrl, String otherUrl, String limit, String section) {
            this.baseUrl = baseUrl;
            this.otherUrls = otherUrl;
            this.section = section;
            this.limit = Integer.parseInt(limit);
             webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setCssEnabled(false);//if you don't need css
            webClient.getOptions().setJavaScriptEnabled(false);//if you don't need js
          
            params = new ArrayList<>();
           
        }
        
        @Override
        public Set<MspProductUrl> call() throws Exception {
            Set<MspProductUrl> s_msp_threadLoacal = new LinkedHashSet<>();
            HtmlPage page = webClient.getPage(baseUrl);
            System.out.println("Running for Page 1 ");
            
            
            List<HtmlAnchor> listTh =(List<HtmlAnchor>) page.getByXPath("//a[contains(@class,'prdct-item__name')]");
            for(int i = 0; i < listTh.size();i++){
                HtmlAnchor elem = listTh.get(i);
                     productUrl =  elem.getAttribute("href").toString(); 
                     
                     MspProductUrl mspProdUrl = new MspProductUrl();
                     mspProdUrl.setUrl(productUrl);
                     mspProdUrl.setSection( this.section);
                     mspProdUrl.setProductId("elecaap");
                     mspProdUrl.setStatus("i");
                     s_msp_threadLoacal.add(mspProdUrl);
            } 
            
            
            /*
            for (int i = 1; i <= 51; i++) {
                try {
                   
                    if (driver.findElements(By.xpath("/html/body/div[4]/div[3]/div[1]/div[5]/div[2]/div[1]/div[" + i + "]/div[2]/a")).size() != 0)
                        productUrl = driver.findElement(By.xpath("/html/body/div[4]/div[3]/div[1]/div[5]/div[2]/div[1]/div[" + i + "]/div[2]/a")).getAttribute("href");
                    
                    else if (driver.findElements(By.xpath("/html/body/div[4]/div[3]/div[1]/div[3]/div[2]/div[1]/div[" + i + "]/div[2]/a")).size() != 0)
                        
                        productUrl = driver.findElement(By.xpath("/html/body/div[4]/div[3]/div[1]/div[3]/div[2]/div[1]/div[" + i + "]/div[2]/a")).getAttribute("href");
                    
                    else
                        productUrl = driver.findElement(By.xpath("/html/body/div[4]/div[3]/div[1]/div[4]/div[2]/div[1]/div[" + i + "]/div[2]/a")).getAttribute("href");
                    
                    // if (!allExistingUrl.contains(productUrl))
                    // this.saveData(productUrl, section);
                    MspProductUrl mspProdUrl = new MspProductUrl();
                    mspProdUrl.setUrl(productUrl);
                    mspProdUrl.setSection( this.section);
                    mspProdUrl.setProductId("elecaap");
                    mspProdUrl.setStatus("i");
                    s_msp_threadLoacal.add(mspProdUrl);
                }
                catch (Exception e) {
                    // System.out.println("Section " + section);
                    
                    continue;
                }
            }
            */
            System.out.println("For All pages Limit " + limit);
            
            // for the otherUrls
            for (int j = 2; j <= limit; j++) {
                System.out.println("Running for Page " + j);
                //driver.get(otherUrls + j + ".html");
                page = webClient.getPage(otherUrls + j + ".html");
               // for (int i = 1; i <= 48; i++) {
                listTh =(List<HtmlAnchor>) page.getByXPath("//a[contains(@class,'prdct-item__name')]");
                for(int i = 0; i < listTh.size();i++){
                    HtmlAnchor elem = listTh.get(i);
                         productUrl =  elem.getAttribute("href").toString(); 
                
                    try {
                        
                         MspProductUrl mspProdUrl = new MspProductUrl();
                         mspProdUrl.setUrl(productUrl);
                         mspProdUrl.setStatus("i");
                         mspProdUrl.setProductId("elecaap");
                         mspProdUrl.setSection(this.section);
                        s_msp_threadLoacal.add(mspProdUrl);
                    }
                    catch (Exception e) {
                         System.out.println("error Section ");
                        
                        continue;
                    }
                }
            }
            
           // driver.close();
            webClient.closeAllWindows();
           
            return s_msp_threadLoacal;
        }
        
    }
    
}
