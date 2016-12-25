package com.sourcecode.standalone;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.evocatus.amazonclient.ItemLookupSample;

public class AmazonProductPriceUpdater {

	private static String host_prd = "jdbc:mysql://209.99.16.94:3306/aapcow9a_dbaapcompare9";
	private static String userName_prd = "aapcow9a_adbuser";
	private static String password_prd = "Admin@1234$";



	private static String host = "jdbc:mysql://localhost:3306/aapcompare";
	private static String userName = "root";
	private static String password = "";
	private static Connection con;
	private static ResultSet rs;
	public static void main(String[] args) {
		ItemLookupSample sampel = new ItemLookupSample();

		List pidList = new ArrayList<>(); 
		BufferedWriter writer = null;
		//load data from FIle to check if that price is already fetched or not.

		File f = new File("C:\\aap_sql\\amazonpid_price_data.sql");
		if(f.exists() && !f.isDirectory()) {
			try (BufferedReader br = new BufferedReader(new FileReader("C:\\aap_sql\\amazonpid_price_data.sql")))
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
			con = DriverManager.getConnection(host, userName, password);
			con.setAutoCommit(false);

			String sql = "select product_id, flipkart_product_id as asin from msp_electronics where website = 'amazon' AND resolved_url NOT LIKE '%www.mysmartprice.com%' and flipkart_product_id is not null limit 5000";

			rs = con.createStatement().executeQuery(sql);
			Map<String, String> mspAmazonProductMap = new HashMap<>();

			while (rs.next()) {
				mspAmazonProductMap.put(rs.getString("asin"),rs.getString("product_id"));
			}
			Statement stmt = con.createStatement();

			int count = 0;
			mspAmazonProductMap =  mspAmazonProductMap.entrySet().stream()
					.filter(s->s.getKey()!=null || s.getKey() !="")
					.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

			System.out.println("Running for map of size .. "+ mspAmazonProductMap.size());


			for(Object tempId :pidList){
				mspAmazonProductMap.remove(tempId);
			}
			System.out.println("Running for new map of size .. "+ mspAmazonProductMap.size());
			try {
				File file =new File("C:\\aap_sql\\amazonpid_price_data.sql");

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

			Set<String> productSet = mspAmazonProductMap.keySet();
			for(String productId:productSet){
				count ++;
				String amazonPrdId= productId;
				String updatedPrice = sampel.getPrice("you id", "your key", "tag-21", amazonPrdId);
				Thread.sleep(1000);
				String price = updatedPrice.replaceAll("\\D+.","");

				System.out.println(amazonPrdId +"  "+price);
				if(price == ""){
					price = "0";
				}

					String insertQuery  = "insert into  amazon_price_update (vendor_product_id, price,vendor) values ('"+amazonPrdId+"','"+price+"','amazon');";
					//String insertQuery  = "update msp_electronics set price =  "+Float.parseFloat(updatedPrice.replaceAll("\\D+.",""))+" , amazon_priceUpdate_flg = 'T' where  flipkart_product_id ='"+mspAmazonProductMap.get(productId)+"'";
					writer.write(insertQuery);
					writer.newLine();
				

			}

		}catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				writer.close();
				con.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

