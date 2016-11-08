package com.sourcecode.standalone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import com.evocatus.amazonclient.ItemLookupSample;

public class AmazonProductPriceUpdater {

	 private static String host_prd = "jdbc:mysql://209.99.16.94:3306/aapcow9a_dbaapcompare9";
     private static String userName_prd = "aapcow9a_adbuser";
     private static String password_prd = "Admin@1234$";
     private static Connection con;
     private static ResultSet rs;

	public static void main(String[] args) {
		ItemLookupSample sampel = new ItemLookupSample();
		
			try { 
	        	   Class.forName("com.mysql.jdbc.Driver");
	               con = DriverManager.getConnection(host_prd, userName_prd, password_prd);
	               con.setAutoCommit(false);
	               
                   String sql = "select product_id, SUBSTRING_INDEX(SUBSTRING_INDEX(resolved_url,'http://www.amazon.in/gp/product/',-1),'/?tag=aapcompare0f-21',1) as asin from msp_electronics where website = 'amazon' AND resolved_url NOT LIKE '%www.mysmartprice.com%' and amazon_priceUpdate_flg = 'F' LIMIT 2";
                   
                   rs = con.createStatement().executeQuery(sql);
                   Map<String, String> mspAmazonProductMap = new HashMap<>();
                   
                   while (rs.next()) {
                	   mspAmazonProductMap.put(rs.getString("product_id"),rs.getString("asin"));
                   }
                   System.out.println("Running for map of size .. "+ mspAmazonProductMap.size());
       			   Statement stmt = con.createStatement();
       			   
       			   int count = 0;

       			   Set<String> productSet = mspAmazonProductMap.keySet();
       			   for(String productId:productSet){
       				count ++;
						String updatedPrice = sampel.getPrice("AKIAIFWKPIPO5WHWWWAA", "obNfhGF28XQzRBQhiD3WdUXpSo4NLjnOD+jDtUUW", "aapcompare0f-21", mspAmazonProductMap.get(productId));
						Thread.sleep(1000);
						System.out.println(mspAmazonProductMap.get(productId) +"  "+updatedPrice.replaceAll("\\D+",""));
						if(updatedPrice == "")
							updatedPrice = "0.0";
						String updateQuery  = "update msp_electronics set price =  "+Float.parseFloat(updatedPrice.replaceAll("\\D+",""))+" , amazon_priceUpdate_flg = 'T' where SUBSTRING_INDEX(SUBSTRING_INDEX(resolved_url,'http://www.amazon.in/gp/product/',-1),'/?tag=aapcompare0f-21',1) = '"+mspAmazonProductMap.get(productId)+"'";
						
						stmt.addBatch(updateQuery);
						System.out.println(updateQuery);
						//
						if(count  == 500){
							stmt.executeBatch();
						System.out.println("100 done");
						con.commit();
						count =0;
						}
       			   }
/*                   mspAmazonProductMap.forEach((k,v)->{
                	   count ++;
       				try {
						String updatedPrice = sampel.getPrice("AKIAIFWKPIPO5WHWWWAA", "obNfhGF28XQzRBQhiD3WdUXpSo4NLjnOD+jDtUUW", "aapcompare0f-21", v);
						String updateQuery  = "update msp_electronics set price =  "+updatedPrice+" , amazon_priceUpdate_flg = 'T' where product_id = "+k+"";
						stmt.addBatch(updateQuery);
						if(count  == 500){
							stmt.executeBatch();
						System.out.println("100 done");
						con.commit();
						count =0;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

                   });*/
                   stmt.executeBatch();
					con.commit();

			}catch (Exception e) {
				e.printStackTrace();
			} finally{
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}

}
