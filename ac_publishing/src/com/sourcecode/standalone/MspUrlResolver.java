/**
 * 
 */
package com.sourcecode.standalone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;








import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;






import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.BrowserVersion;

/**
 * @author JN1831
 *  @date 21 Oct 2015
 *  This file is used to resolve all the urls of MSPS
 */

public class MspUrlResolver {
    
    
    
    
    static{
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }
    private static String host = "jdbc:mysql://localhost:3306/aapcompare";
    private static String userName = "root";
    private static String password = "";
    private static Connection conn;

    Map<String, List<OldUrlMap>> urlMap;
	static String idBase = "ACNEW";
	int count;
	private static Map<String, Map<String, List<OldUrlMap>>> mainMap;
	private List<OldUrlMap> urlList;
	
    @Async
    public static void execute(){
        
        try{
            System.out.println("Starting FillMSPProductURlColumns");

            // Load the Driver class. 
            Class.forName("com.mysql.jdbc.Driver");
            // If you are using any other database then load the right driver here.

            //Create the connection using the static getConnection method
            conn= DriverManager.getConnection (host,userName,password);
            conn.setAutoCommit(false);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    	MspUrlResolver mspSpecExtractor = new MspUrlResolver();
		mspSpecExtractor.getUrls();
		HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3_6);
		ExecutorService executor = Executors.newFixedThreadPool(6);
		List<Future<String>> list = new ArrayList<Future<String>>();

		for(Map.Entry<String,Map<String,List<OldUrlMap>>> topLevelMenuMapEntry:mainMap.entrySet()){

		    for(Map.Entry<String, List<OldUrlMap>> sectionLevelEntry:topLevelMenuMapEntry.getValue().entrySet() ){

				Callable<String> callable = mspSpecExtractor.new DataExtractor(sectionLevelEntry.getValue(),sectionLevelEntry.getKey(), driver, idBase);
				
				Future<String> future = executor.submit(callable);
				list.add(future);
		    }
			
		
		}
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.out.println(e);
        }
		System.out.println("Shutting down ececutor");

    }
    
    
    
    
    public void getUrls() {
	    System.out.println("Starting at "+new Timestamp(new Date().getTime()));
		
		

		mainMap = new HashMap<>();

		try {
		    String mainQuery = "select distinct menu_level1 from msp_electronics where url_mapped='F'";
	        Statement stmt = conn.createStatement();
	        Statement stmt1 = conn.createStatement();
	        Statement stmt2 = conn.createStatement();
	        ResultSet rs1 = stmt.executeQuery(mainQuery);
	        
			while (rs1.next()) {
				String subMenuQuery = "SELECT distinct menu_level2 ,section FROM msp_electronics WHERE  menu_level1 = '"
						+ rs1.getString("menu_level1") + "' and url_mapped = 'F' ORDER BY menu_level2";
				// get the prodct corr to each section
				ResultSet rs = stmt1.executeQuery(subMenuQuery);
				urlMap = new HashMap<>();
				while (rs.next()) {
					String getProductUrl = "select * from msp_electronics where menu_level2 = '"
							+ rs.getString(1) + "' and url_mapped = 'F'";
					ResultSet rsProductUrl = stmt2.executeQuery(getProductUrl);
					urlList = new ArrayList<>();
					while (rsProductUrl.next()) {
						urlList.add(new OldUrlMap(rsProductUrl.getString("product_id"),rsProductUrl.getString("website") , rsProductUrl.getString("url")));
					}
					urlMap.put(rs.getString(1), urlList);
				}
				mainMap.put(rs1.getString("menu_level1"), urlMap);
			}
			System.out.println("Ending  at "+new Timestamp(new Date().getTime()));
		} catch (Exception e) {
		    System.out.println("Error at "+new Timestamp(new Date().getTime()));
			e.printStackTrace();
		} finally {

			// conn.closeConnection();
		}
	}
	
    class OldUrlMap{
    	String id;
    	String website;
    	String oldUrl;
    	public OldUrlMap(String id,String website,String oldUrl){
    	  this.id = id; this.website = website; this.oldUrl = oldUrl;	
    	}
		public String getId() {
			return id;
		}
		public String getWebsite() {
			return website;
		}
		public String getOldUrl() {
			return oldUrl;
		}
    	
    	}
    
	
    class DataExtractor implements Callable{
		String query;
		List<String> params;
		
		List<OldUrlMap> url;
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

		// some static variables
		 String website="";
	       
	         String id = "";
	       
	         String oldUrl = "";
	         StringBuilder resolvedUrl = null;
	         
	         
		public DataExtractor(List<OldUrlMap> baseUrl, String section,HtmlUnitDriver driver, String id) {
			this.url = baseUrl;
			this.section = section;
			this.driver = driver;
			this.productid = id;
			params = new ArrayList<>();
			

		}
		@Override
		public Object call() throws Exception {
			Iterator<OldUrlMap> itr = url.iterator();
			OldUrlMap currentUrlObject = null;
	    	 StringBuilder prdSpec = new StringBuilder();
	        
	         
	         // defining loop variables
	         params = new ArrayList<String>();
	         
	         
	         
			while (itr.hasNext()) {
				
				currentUrlObject = itr.next();
				
				try {
					driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3_6);
					id = currentUrlObject.getId();
					oldUrl = currentUrlObject.getOldUrl();
					website = currentUrlObject.getWebsite();
					driver.get(oldUrl);
					
					(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
                        public Boolean apply(WebDriver d) {
                            boolean flag = false;
                            //System.out.println(d.getCurrentUrl());
                            if(d.getCurrentUrl().startsWith("http://www."+website)){
                                
                                flag = true;
                            }else if(d.getCurrentUrl().startsWith("https://"+website)){
                                flag = true;
                                
                            }else
                            {
                                flag = true;
                                
                            }
                        return flag;
                        }
                    });
					
					if(driver.getCurrentUrl().contains("?"))
                    	resolvedUrl = new StringBuilder(driver.getCurrentUrl().substring(0, driver.getCurrentUrl().indexOf("?")));
                    else
                    	resolvedUrl =  new StringBuilder(driver.getCurrentUrl());
                    
                    if(website.equalsIgnoreCase("amazon"))
                        resolvedUrl.append("?tag=aapcompare0f-21");
                    else if(website.equalsIgnoreCase("flipkart"))
                        resolvedUrl.append("?affid=a123pp9aa");
                    else if(website.equalsIgnoreCase("infibeam"))
                        resolvedUrl.append("?trackId=a12");
                    else if(website.equalsIgnoreCase("snapdeal"))
                        resolvedUrl.append("?aff_id=37358");
                    else if(website.equalsIgnoreCase("shopclues"))
                        resolvedUrl.append("?id=756");
                    else if(website.equalsIgnoreCase("indiatimes") || website.equalsIgnoreCase("paytm"))
                        resolvedUrl =   new StringBuilder("http://clk.omgt5.com/?AID=769090&PID=11256&r=").append(resolvedUrl);
					
					
                    String updateQUery = "update msp_electronics set resolved_url = ?, url_mapped = 'T' where product_id = ? and url = ? and website = ?";
                    
                    params.add(resolvedUrl.toString());
                    params.add(id);
                    params.add(oldUrl);
                    params.add(website);
                   
                    upsertData(updateQUery, params);
                    
                    params.clear(); resolvedUrl.delete(0, resolvedUrl.length());
                    

		    		 
				} catch (Exception e) {
					e.printStackTrace();
					String updateQUery = "update msp_electronics set url_mapped = 'N' where product_id = ?";
					upsertData(updateQUery, Arrays.asList(id));
					continue;
					
					
				}finally{
					driver.quit();
				}
			}

			return null;


		}
		public boolean upsertData(String query,List<String> params){
	        PreparedStatement pstmt  = null;
	        boolean flag = false;
	        
	        try {
	        
	            pstmt = conn.prepareStatement(query);
	            if(params!=null){
	            int i = 1;
	            for(String str: params){
	                pstmt.setString(i++, str);
	            }
	        }
	        
//	          /System.out.println(query);
	            if(pstmt.executeUpdate()>0){
	                flag = true;
	            }
	            
	        } catch (SQLException e) {
	            flag = false;
	            e.printStackTrace();
	        }finally{
	            /*try {
	                //pstmt.close();
	                //pstmt1.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }*/
	        }
	        return flag;
	    }
  
    }    
    
}
