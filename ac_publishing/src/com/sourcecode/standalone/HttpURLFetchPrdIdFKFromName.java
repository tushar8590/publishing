package com.sourcecode.standalone;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;

public class HttpURLFetchPrdIdFKFromName {

           private final String USER_AGENT = "Mozilla/5.0";
           private static String host_prd = "jdbc:mysql://209.99.16.94:3306/aapcow9a_dbaapcompare9";
           private static String userName_prd = "aapcow9a_adbuser";
           private static String password_prd = "Admin@1234$";
           private static Connection con;
           
           private static ResultSet rs;

           public static void main(String[] args) throws Exception {
        	   
        	   Class.forName("com.mysql.jdbc.Driver");
               // If you are using any other database then load the right driver here.
               
               // Create the connection using the static getConnection method
               con = DriverManager.getConnection(host_prd, userName_prd, password_prd);
               con.setAutoCommit(false);

                      

           //          System.out.println("\nTesting 2 - Send Http POST request");
                       //http.sendPost();
                       
                       String sql = "select msp_model,SUBSTRING_INDEX(SUBSTRING_INDEX(REPLACE(SUBSTRING_INDEX(resolved_url,'http://www.flipkart.com/',-1),'?pid=COMDNWAGAAYZN5UZ',''),'/p/',1),'https://www.flipkart.com/',-1) as urlPart from msp_electronics where website = 'flipkart' AND resolved_url NOT LIKE '%www.mysmartprice.com%' and flipkart_product_id is null";
                       
                       rs = con.createStatement().executeQuery(sql);
                       Map<String, String> mspModelsMap = new HashMap<>();
                       
                       while (rs.next()) {
                           mspModelsMap.put(rs.getString("msp_model"),rs.getString("urlPart"));
                       }
                       System.out.println("Map Size = " + mspModelsMap.size());
                       HttpURLFetchPrdIdFKFromName http = new HttpURLFetchPrdIdFKFromName();
                       Map<String, String> mspModelsMapWithID = new HashMap<>();
                       BufferedWriter writer = new BufferedWriter(new FileWriter(new File("C:\\Users\\LENOVO\\Documents\\data_fk_updateid.txt")));
                       mspModelsMap.forEach((k,v) -> {
                           
                    	   String prdId;
						try {
							prdId = http.sendGet(v);
							
							 String str = "update msp_electronics set flipkart_product_id = '"+prdId +"' where msp_model = '"+k+"' and website ='flipkart';";
		                 	 try {
		                          writer.write(str);
		                          writer.write("\r\n");
		                      }
		                      catch (Exception e) {
		                           e.printStackTrace();
		                      }
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                       });
                       writer.close();
                     
                /* mspModelsMapWithID.forEach((k,v) -> {
                 
                 	 String str = "update msp_electronics set flipkart_product_id = '"+v +"' where msp_model = '"+k+"' and website ='flipkart';";
                 	 try {
                          writer.write(str);
                          writer.write("\r\n");
                      }
                      catch (Exception e) {
                           e.printStackTrace();
                      }
               
                 	
                 }); */
              
                       

           }

           // HTTP GET request
           private String sendGet(String prdName) throws Exception {
        	   String prdId = null;
        	   			try{
                       String url = "https://affiliate-api.flipkart.net/affiliate/search/json?query="+prdName+"&resultCount=1";
                       //String url = "https://affiliate-api.flipkart.net/affiliate/product/json?id=itme49ddpvzwzfer";
                       URL obj = new URL(url);
                       HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                       // optional default is GET
                       con.setRequestMethod("GET");

                       //add request header
                       con.setRequestProperty("User-Agent", "PHP-ClusterDev-Flipkart/0.1");
                       con.addRequestProperty("Fk-Affiliate-Id", "a123pp9aa");
                       con.addRequestProperty("Fk-Affiliate-Token", "3523e24e9a5047f7ab2c0dceef27a84c");
                       con.setRequestProperty("Cache-Control","no-cache");

                       int responseCode = con.getResponseCode();
                      // System.out.println("\nSending 'GET' request to URL : " + url);
                      // System.out.println("Response Code : " + responseCode);

                       BufferedReader in = new BufferedReader(
                              new InputStreamReader(con.getInputStream()));
                       String inputLine;
                       StringBuffer response = new StringBuffer();

                       while ((inputLine = in.readLine()) != null) {
                                   response.append(inputLine);
                       }
                       in.close();

						String tempString = response.toString();
						//System.out.println(tempString.toString());
						 prdId= StringUtils.substringBetween(tempString, "productId", "categoryPaths").replace("productId", "").replaceAll("[^a-zA-Z0-9 ]", "").replace("entifier", "");
						//System.out.println( prdId);

						}
				catch(Exception e){
					
				}
        	   			return prdId;
           }

           // HTTP POST request
           private void sendPost() throws Exception {

                       String url = "https://selfsolve.apple.com/wcResults.do";
                       URL obj = new URL(url);
                       HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

                       //add reuqest header
                       con.setRequestMethod("POST");
                       con.setRequestProperty("User-Agent", USER_AGENT);
                       con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                       String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

                       // Send post request
                       con.setDoOutput(true);
                       DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                       wr.writeBytes(urlParameters);
                       wr.flush();
                       wr.close();

                       int responseCode = con.getResponseCode();
                       System.out.println("\nSending 'POST' request to URL : " + url);
                       System.out.println("Post parameters : " + urlParameters);
                       System.out.println("Response Code : " + responseCode);

                       BufferedReader in = new BufferedReader(
                              new InputStreamReader(con.getInputStream()));
                       String inputLine;
                       StringBuffer response = new StringBuffer();

                       while ((inputLine = in.readLine()) != null) {
                                   response.append(inputLine);
                       }
                       in.close();

                       //print result
                       System.out.println(response.toString());

           }

}
