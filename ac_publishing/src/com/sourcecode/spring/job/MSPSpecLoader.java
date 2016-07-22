package com.sourcecode.spring.job;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.sourcecode.spring.job.MSPUrlExtractor.DataExtractor;
import com.sourcecode.spring.model.MspElectronics;
import com.sourcecode.spring.model.MspProductUrl;
import com.sourcecode.spring.service.MspCatDataService;
import com.sourcecode.spring.service.MspSpecLoaderService;

@Component
public class MSPSpecLoader {
    
    
    
    Map<String,String> urlMap;
 
    List<String> params;
    
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

    static{
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
       }
    
/*    public MSPSpecLoader(){
        params = new ArrayList<>();
    }
    
    public static void main(String[] args) {
        System.out.println("Starting at "+new Timestamp(new Date().getTime()));
        MSPSpecLoader obj = new MSPSpecLoader();
       // System.out.println(obj.getUrlMap().size());
        obj.getDataFromMap(obj.getUrlMap());
        
        System.out.println("Ending  at "+new Timestamp(new Date().getTime()));
    }
    
    
    *//**
     * This method will return the sno and url of the records with temp_flag = 'F'
     * @return
     *//*    
    public Map<String,String> getUrlMap() {
        String query = "SELECT sno,spec_url,product_spec FROM  msp_product_url WHERE temp_flag = 'F' AND section = 'mobiles' AND product_spec IS NULL  OR product_spec = '' LIMIT 500";
        urlMap = new HashMap<>();
        ResultSet rs = conn.executeQuery(query, null);
        try {
            while(rs.next()){
                urlMap.put(rs.getString("sno"), rs.getString("spec_url"));
            }
        }
        catch (SQLException e) {
            
            e.printStackTrace();
        }
        System.out.println(urlMap.size());
        return urlMap;
    }
    
    
    public void getDataFromMap(Map<String,String> map){
       
        StringBuilder prdSpec = new StringBuilder();
     Set<String> keySet = map.keySet();
     for(String s:keySet){
         String v = map.get(s);
        //map.forEach((k,v) -> {
            try {
                driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_24);
                driver.get(v);
                
                String  header = "";
                 String  key = "";
                 String  value = "";
                 String combinedVal[] =null; 
                 
                 
                 
                       for(int i=1;i<=60;i++)
                         {
                             try{
                                 
                                 String divVal = driver.findElement(By.xpath("//*[@id='msp_body']/div/div[5]/div[2]/div[1]/div/table/tbody/tr["+i+"]")).getText();
                               
                              
                               //*[@id="msp_body"]/div/div[5]/div[2]/div[1]/div/table/tbody/tr[4]
                                 if(divVal.split("\n").length > 1){ // its key - value 
                                      combinedVal = driver.findElement(By.xpath("/html/body/div[4]/div[3]/div["+i+"]")).getText().split("\n");
                                      key = combinedVal[0];
                                      value = combinedVal[1];
                                      prdSpec.append(key+"|");
                                      prdSpec.append(value+";");
                                 }else{
                                     header = driver.findElement(By.xpath("/html/body/div[4]/div[3]/div["+i+"]")).getText();
                                     prdSpec.append("#"+header+";");
                                 }
                     
                             
                             }
                            
                             catch(Exception e){
                                 e.printStackTrace();
                                 saveSkipForNoData(v);
                                  continue;}
        
                         }

            // if all goes well save the data   
                       //  System.out.println(prdSpec.toString());
            this.saveData(prdSpec.toString(), v);
   
            } catch (Exception e) {
                e.getMessage();
                saveSkipForNoData(v);
                
            }finally{
                driver.quit();
                prdSpec.delete(0,prdSpec.length());
            }
            
            
            
            
            
            
            
            
                
        }
    }
    
    private void saveData(String prdSpec,String currentUrl) {
      String  query = SQLQueries.updateMSPSpec;
        if(prdSpec == null)
            prdSpec = " ";
        params.add(prdSpec);
        params.add(currentUrl);
         conn.upsertData(query, params);
        params.clear();
         System.out.println(currentUrl);
    }
    
    private void saveSkipForNoData(String currentUrl){
        System.out.println("No Data");
        String  query = SQLQueries.updateSkipForNoSpecData; 
        params.clear();
        params.add(currentUrl);
         conn.upsertData(query, params);
            params.clear();
    }
     */
    
    @Autowired
    private MspSpecLoaderService specLoaderService;
    
    private Map<String,List<MspProductUrl>> productUrlSpecMap;
    static int breakFactor = 12;
    
    
    
    private void populateSpecUrlMap(List<String> categories){
        productUrlSpecMap = specLoaderService.populateUrlSpecLoaderMap(categories);
        System.out.println(productUrlSpecMap);
    }
    
    @Async
    public void processData(List<String> sections) {
        this.populateSpecUrlMap(sections);
        this.processMap();
    }
    
    private void processMap(){
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        Set<Future<List<MspProductUrl>>> futureSet = new HashSet<Future<List<MspProductUrl>>>();
        productUrlSpecMap.forEach((k, v) -> {
            // subdivide the arraylist corresponding to each section k
            int a = 0;
            int b = v.size() / breakFactor;
            
            for (int i = 0; i < breakFactor; i++) {
                List<MspProductUrl> listn = v.subList(a, b);
                
                a = b;
                b = b + v.size() / breakFactor;
                
                Callable<List<MspProductUrl>> callable = this.new DataExtractor(k,listn);
                Future<List<MspProductUrl>> future = executor.submit(callable);
                futureSet.add(future);
            }
            
            List<MspProductUrl> listn = v.subList(a, v.size());
            
            
            Callable<List<MspProductUrl>> callable = this.new DataExtractor(k,listn);
            Future<List<MspProductUrl>> future = executor.submit(callable);
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
        System.out.println("Finished");
       // specLoaderService.flushSession();
    }
    
    
    class DataExtractor implements Callable<List<MspProductUrl>> {

        String section;
        List<MspProductUrl> specUrlList;
        HtmlUnitDriver driver;
        
        DataExtractor(String section,List<MspProductUrl> specUrlList){
            this.section = section;
            this.specUrlList = specUrlList;
            driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3_6);
        }
        @Override
        public  List<MspProductUrl> call() throws Exception {
            System.out.println("I got size " + specUrlList.size());
            specUrlList.forEach((m)->{
                StringBuilder prdSpec = new StringBuilder();
                driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3_6);
                driver.get(m.getSpecURL());
                
                String  header = "";
                 String  key = "";
                 String  value = "";
                 String combinedVal[] =null; 
                      for(int i=1;i<=60;i++)
                         {
                             try{
                                 
                                 String divVal = driver.findElement(By.xpath("//*[@id='msp_body']/div/div[5]/div[2]/div[1]/div/table/tbody/tr["+i+"]")).getText();
                               //*[@id="msp_body"]/div/div[5]/div[2]/div[1]/div/table/tbody/tr[4]
                                 if(divVal.split("\n").length > 1){ // its key - value 
                                      combinedVal = driver.findElement(By.xpath("/html/body/div[4]/div[3]/div["+i+"]")).getText().split("\n");
                                      key = combinedVal[0];
                                      value = combinedVal[1];
                                      prdSpec.append(key+"|");
                                      prdSpec.append(value+";");
                                 }else{
                                     header = driver.findElement(By.xpath("/html/body/div[4]/div[3]/div["+i+"]")).getText();
                                     prdSpec.append("#"+header+";");
                                 }

                             }catch(Exception e){
                                 System.out.println(e.getMessage());
                                 continue;
                             }
                         }
              //   prdSpec.append("Some value");
                 m.setProductSpec(prdSpec.toString());
             });
            specLoaderService.saveMspUrlsToBeInserted(specUrlList);
            
            return specUrlList;
        }
        
    }
}
