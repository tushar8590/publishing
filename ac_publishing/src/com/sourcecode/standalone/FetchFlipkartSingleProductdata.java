package com.sourcecode.standalone;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FetchFlipkartSingleProductdata {


	//private final String USER_AGENT = "Mozilla/5.0";

	private static String host = "jdbc:mysql://localhost:3306/aapcompare";
	private static String userName = "root";
	private static String password = "";
	private static Connection con;
	private static ResultSet rs;
	

	public static void main(String arg[])
	{
		List pidList = new ArrayList<>(); 
		BufferedWriter writer = null;
		//load data from FIle to check if that price is already fetched or not.
		
		File f = new File("C:\\aap_sql\\fkpid_price_data.sql");
		if(f.exists() && !f.isDirectory()) {
			try (BufferedReader br = new BufferedReader(new FileReader("C:\\aap_sql\\fkpid_price_data.sql")))
			{

				String sCurrentLine;
				
				while ((sCurrentLine = br.readLine()) != null) {
					String[] pdi = sCurrentLine.split("'");
					pidList.add(pdi[1]);
				
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		Set<String> productSet = null;
		
		try { 
			Class.forName("com.mysql.jdbc.Driver");
			// con = DriverManager.getConnection(host_prd, userName_prd, password_prd);
			con = DriverManager.getConnection(host, userName, password);

			con.setAutoCommit(false);

			String sql = "select product_id, flipkart_product_id from msp_electronics where website = 'flipkart' AND resolved_url NOT LIKE '%www.mysmartprice.com%' and flipkart_product_id is not null ";

			rs = con.createStatement().executeQuery(sql);
			Map<String, String> mspSnapdealProductMap = new HashMap<>();

			while (rs.next()) {
				mspSnapdealProductMap.put(rs.getString("flipkart_product_id"),rs.getString("product_id"));
			}
			System.out.println("Running for map of size .. "+ mspSnapdealProductMap.size());
			
			for(Object tempId :pidList){
			mspSnapdealProductMap.remove(tempId);
			}
			
			System.out.println("Running for map of size .. "+ mspSnapdealProductMap.size());

			productSet = mspSnapdealProductMap.keySet();
			//HttpURLConnectionSD http = new HttpURLConnectionSD();
			
			//writer = new BufferedWriter(new FileWriter(new File("C:\\aap_sql\\fkpid_price_data.sql"),true));
		}catch(Exception e){
			e.printStackTrace();

		}
		List tempDataList = new ArrayList<>();
		try {
			File file =new File("C:\\aap_sql\\fkpid_price_data.sql");

    		//if file doesnt exists, then create it
    		if(!file.exists()){
    			file.createNewFile();
    		}

    		//true = append file
    		FileWriter fileWritter = new FileWriter(file,true);
    		 writer  = new BufferedWriter(fileWritter);
		}catch(Exception e){
			e.printStackTrace();

		}
		for(String productId:productSet){
			try { 
				String url = "https://affiliate-api.flipkart.net/affiliate/product/json?id="+productId;
				URL obj;

				obj = new URL(url);

				HttpURLConnection con = (HttpURLConnection) obj.openConnection();

				// optional default is GET
				con.setRequestMethod("GET");

				//add request header
				con.setRequestProperty("User-Agent", "PHP-ClusterDev-Flipkart/0.1");
				con.addRequestProperty("Fk-Affiliate-Id", "a123pp9aa");
				con.addRequestProperty("Fk-Affiliate-Token", "3523e24e9a5047f7ab2c0dceef27a84c");
				con.setRequestProperty("Cache-Control","no-cache");



				//int responseCode = con.getResponseCode();
				//System.out.println("\nSending 'GET' request to URL : " + url);
				//System.out.println("Response Code : " + responseCode);

				BufferedReader in = new BufferedReader(
						new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
					//System.out.println(inputLine);
				}

				JSONParser parser = new JSONParser();
				Object objData = parser.parse(response.toString());
				JSONObject jsonObject = (JSONObject) objData;
				if ( jsonObject instanceof JSONObject ) {

					//Get FK Product ID
					JSONObject productBaseInfo = (JSONObject) jsonObject.get("productBaseInfo");
					JSONObject productIdentifier = (JSONObject) productBaseInfo.get("productIdentifier");
					String id = (String) productIdentifier.get("productId");

					// Get FK Selling price
					JSONObject productAttributes = (JSONObject) productBaseInfo.get("productAttributes");
					JSONObject sellingPrice = (JSONObject) productAttributes.get("sellingPrice");
					double price =  (double) sellingPrice.get("amount");

					String str = "insert into  flipkart_price_update (vendor_product_id, sd_price,vendor) values ('"+id+"','"+price+"','flipkart');";
					//System.out.println(str);
					
					 writer.append(str);
					writer.newLine();
			}
				in.close();
			}

			catch(Exception e){
				e.printStackTrace();
				continue;
			}
			
		}
		try{
    	        
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Testing 1 - Send Http GET request");
		}
}
	
