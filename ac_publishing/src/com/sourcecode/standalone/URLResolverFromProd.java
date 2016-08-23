package com.sourcecode.standalone;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class URLResolverFromProd {

	
	private static String host = "jdbc:mysql://209.99.16.94:3306/aapcow9a_aapcompare_stg";
	private static String userName = "aapcow9a_adbuser";
	private static String password = "Admin@1234$";
	private static Connection con;
	private ResultSet rs;
	
	private static String hostlocal = "jdbc:mysql://localhost:3306/aapcompare";
	private static String userNamelocal = "root";
	private static String passwordlocal = "";
	private static Connection conlocal;
	private ResultSet rslocal;
	


	public static void main(String args[]){
		Map  map = new HashMap();
		try{

			Class.forName("com.mysql.jdbc.Driver");
			// If you are using any other database then load the right driver here.
			//Create the connection using the static getConnection method
			con = DriverManager.getConnection (host,userName,password);
			con.setAutoCommit(false);
			

			String query ="select  url, resolved_url from msp_electronics where resolved_url like '%http%' ";
			Statement stmt = con.createStatement();
			
			ResultSet rs;
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				String mainUrl=rs.getString("url");
				
				mainUrl = mainUrl.replaceAll("rk=.*&", "");
				mainUrl = mainUrl.replaceAll("&id=.*&", "&");

				String resolvedUrl=rs.getString("resolved_url");
				map.put(mainUrl, resolvedUrl);

			}
			System.out.println(map.size());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		try{
			int batchSize = 100;
			
			int count =0;
			
			// If you are using any other database then load the right driver here.
			//Create the connection using the static getConnection method
			conlocal = DriverManager.getConnection (hostlocal,userNamelocal,passwordlocal);
			conlocal.setAutoCommit(false);
			//String fileName="C:/Users/jt3411/Downloads/277-29042015-120845.csv";  
			//Statement stmt  = null;
			Statement stmtlocal = conlocal.createStatement();
		
			String querylocal = "select  url from msp_electronics where url <> '' and resolved_url is null";
			Map  maplocal = new HashMap();
			ResultSet rslocal;
			rslocal = stmtlocal.executeQuery(querylocal);
			while(rslocal.next())
			{
				String mainUrllocal=rslocal.getString("url");		
				
				String mainUrllocalTemp =mainUrllocal;
				mainUrllocalTemp = mainUrllocalTemp.replaceAll("rk=.*&", "");
				mainUrllocalTemp = mainUrllocalTemp.replaceAll("&id=.*&", "&");

				if(map.containsKey(mainUrllocalTemp)){
					//System.out.println("exists");
				String resolvedUrlProd=map.get(mainUrllocalTemp).toString();
				maplocal.put(mainUrllocal, resolvedUrlProd);
				
				}

			}
			
			System.out.println("Map populated");
			Iterator entries = maplocal.entrySet().iterator();
			System.out.println("Map Size"+maplocal.size());

			while (entries.hasNext()) {
				count++;
			  Entry thisEntry = (Entry) entries.next();
			  String key = thisEntry.getKey().toString();
			  String value = thisEntry.getValue().toString();
			  if(value.contains("'")){
				 value = value.replace("'", "\\'");
					  
			}
			  //System.out.println(value);
				String updatURLQuery =  "update msp_electronics set resolved_url ='"+ value +"' where url = '"+ key +"'";
				stmtlocal.addBatch(updatURLQuery);
				if(count  == 500){
				stmtlocal.executeBatch();
				System.out.println("100 done");
				conlocal.commit();
				count =0;
				}
				//System.out.println(updatURLQuery);
			  // ...
			}
			stmtlocal.executeBatch();

		
			
			/*Statement stmtlocal = conlocal.createStatement();
			Map  maplocal = new HashMap();
			ResultSet rslocal;
			rslocal = stmtlocal.executeQuery(querylocal);
			while(rslocal.next())
			{
				String mainUrllocal=rslocal.getString("url");
				if(map.containsKey(mainUrllocal)){
				String resolvedUrlProd=map.get(mainUrllocal).toString();
				maplocal.put(mainUrllocal, resolvedUrlProd);
				
				}

			}*/
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
