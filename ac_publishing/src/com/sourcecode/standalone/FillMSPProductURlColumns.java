package com.sourcecode.standalone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;




public class FillMSPProductURlColumns {

    static{
	        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
	        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
	    }
	    private static String host = "jdbc:mysql://localhost:3306/aapcorjr_dbaapcompare9";
	    private static String userName = "root";
	    private static String password = "";
	    private static Connection con;




	private ResultSet rs;

	public static void execute(){
		try{
		    System.out.println("Starting FillMSPProductURlColumns");

			// Load the Driver class. 
			Class.forName("com.mysql.jdbc.Driver");
			// If you are using any other database then load the right driver here.

			//Create the connection using the static getConnection method
			con = DriverManager.getConnection (host,userName,password);
			con.setAutoCommit(false);

		}catch(Exception e){
			e.printStackTrace();
		}
		try {
			String query ="SELECT DISTINCT menu_level1, menu_level2, section FROM new_menu ";

			String sectionQuery ="SELECT DISTINCT section FROM msp_product_url WHERE STATUS = 'i'";

			Statement stmt = con.createStatement();
			Statement stmt1 = con.createStatement();


			ResultSet rs;

			rs = stmt.executeQuery(query);
			Map<String,String> columnMappingMap = new HashMap<>();
			while(rs.next())
			{
				try {
					String menu_level1 =rs.getString("menu_level1");

					String menu_level2 =rs.getString("menu_level2");
					String section =rs.getString("section");

					columnMappingMap.put(section, menu_level1+"*"+menu_level2);


				}
				catch(Exception e){
					continue;
		  		}
  
			}

			rs = stmt1.executeQuery(sectionQuery);

  

			while(rs.next())
			{
				String section = rs.getString("section");

				if(columnMappingMap.containsKey(section)){
					String value = columnMappingMap.get(section);
					String[] parts = value.split("\\*");
					//saveData(model,parts[0],parts[1],spec_url,section);
					List<String> params = new ArrayList<>();
					query = "update msp_product_url set model = SUBSTRING_INDEX(url,'/',-1),menu_level1 = ?,menu_level2 = ?,spec_url = REPLACE(REPLACE(CONCAT('http://www.mysmartprice.com/product/',SUBSTRING_INDEX(url,'/',-2),'-other#tab_spec'),'msf','mst'),'msp','mst'),temp_flag = 'f' where section = ? and status = 'i' and model is null";

					params.add(parts[0]);
					params.add(parts[1]);
					params.add(section);
					upsertData(query, params);
					System.out.println("inserted");
					params.clear();
				}

			}

			//System.out.println(columnMappingMap.size());
			// udpate product_id
			String updateQuery = "update msp_product_url set product_id  = CONCAT(product_id,sno) WHERE STATUS  = 'i' ";
			stmt1.executeUpdate(updateQuery);
			System.out.println("FillMSPProductURlColumns completed");
		} catch (SQLException e) {
		
			e.printStackTrace();

		}finally{
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static boolean upsertData(String query,List<String> params){
        PreparedStatement pstmt  = null;
        boolean flag = false;
        
        try {
       
            pstmt = con.prepareStatement(query);
            if(params!=null){
            int i = 1;
            for(String str: params){
                pstmt.setString(i++, str);
            }
        }
        
//          /System.out.println(query);
            if(pstmt.executeUpdate()>0){
                flag = true;
            }
            
        } catch (SQLException e) {
            flag = false;
            e.printStackTrace();
        }
        
        return flag;
    }

}
