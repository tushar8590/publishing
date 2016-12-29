
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

import com.sourcecode.spring.PriceUpdatertype;

public class SnapdealProductPriceUpdater extends PriceUpdater{

	@Override
	public  void execute(PriceUpdatertype updaterType)
	{
		System.out.println(System.currentTimeMillis());
		
		List pidList = new ArrayList<>(); 
		BufferedWriter writer = null;
		//load data from FIle to check if that price is already fetched or not.
		
		File f = new File("C:\\aap_sql\\sdpid_price_data.sql");
		if(f.exists() && !f.isDirectory()) {
			try (BufferedReader br = new BufferedReader(new FileReader("C:\\aap_sql\\sdpid_price_data.sql")))
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

		try { 
			Class.forName("com.mysql.jdbc.Driver");
			// con = DriverManager.getConnection(host_prd, userName_prd, password_prd);
			con = DriverManager.getConnection(host, userName, password);

			con.setAutoCommit(false);

			String sql = null;
			if(updaterType.equals(PriceUpdatertype.WEEKLY))
				 sql = "select product_id, flipkart_product_id from msp_electronics where website = 'snapdeal' AND resolved_url NOT LIKE '%www.mysmartprice.com%' and flipkart_product_id is not null";
			else
				 sql = "select product_id, flipkart_product_id from msp_electronics where website = 'snapdeal' AND resolved_url NOT LIKE '%www.mysmartprice.com%' and flipkart_product_id is not null  and section in ('mobiles','tablets','laptops')";
			rs = con.createStatement().executeQuery(sql);
			Map<String, String> mspSnapdealProductMap = new HashMap<>();

			while (rs.next()) {
				mspSnapdealProductMap.put(rs.getString("flipkart_product_id"),rs.getString("product_id"));
			}
			System.out.println("Running for map of size .. "+ mspSnapdealProductMap.size());

			
			for(Object tempId :pidList){
				mspSnapdealProductMap.remove(tempId);
				}
			System.out.println("Running for new map of size .. "+ mspSnapdealProductMap.size());

			Set<String> productSet = mspSnapdealProductMap.keySet();
			//HttpURLConnectionSD http = new HttpURLConnectionSD();
			// writer = new BufferedWriter(new FileWriter(new File("C:\\aap_sql\\sdpid_price_data.sql")));
			
			try {
				File file =new File("C:\\aap_sql\\sdpid_price_data.sql");

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

				String url = "http://affiliate-feeds.snapdeal.com/feed/product?id="+productId;
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();

				// optional default is GET
				con.setRequestMethod("GET");

				//add request header
				//con.setRequestProperty("User-Agent", "PHP-ClusterDev-Flipkart/0.1");
				con.addRequestProperty("Snapdeal-Affiliate-Id", "37358");
				con.addRequestProperty("Snapdeal-Token-Id", "4a58c9fafca79d272c1eb166f64768");
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
					/*System.out.print(((JSONObject) jsonObject).get("id"));
			            	System.out.print("          ");
			            	System.out.print(((JSONObject) jsonObject).get("offerPrice"));
			            	System.out.print("          ");
			            	System.out.println(((JSONObject) jsonObject).get("link"));*/

					long id =  (long) ((JSONObject) jsonObject).get("id");
					long price = (long) ((JSONObject) jsonObject).get("offerPrice");
					//String link = (String) ((JSONObject) jsonObject).get("link");

					String str = "insert into  snapdeal_price_update (vendor_product_id, sd_price,vendor) values ('"+id+"','"+price+"','snapdeal');";
					writer.write(str);
					writer.newLine();
				}
				in.close();
			}
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
			
		}
		System.out.println(System.currentTimeMillis());

		System.out.println("Testing 1 - Send Http GET request");
	}

}