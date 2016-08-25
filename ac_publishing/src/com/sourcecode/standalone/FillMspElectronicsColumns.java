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

public class FillMspElectronicsColumns {

	 static{
	        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
	        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
	    }
	    private static String host = "jdbc:mysql://localhost:3306/aapcompare";
	    private static String userName = "root";
	    private static String password = "";
	    private static Connection con;




	private ResultSet rs;

	public static void execute(List<String> sections){
		try{
			System.out.println("Starting FillMSPElectronicColumns");
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
					query = "update msp_electronics set  menu_level1 = ?, menu_level2 = ?, brand = SUBSTRING_INDEX(model, ' ', 1), website = SUBSTRING_INDEX(url, 'store=',-1),image = CONCAT('aap_product_images_normal/aapcompare_',REPLACE(msp_model,CONCAT('-',SUBSTRING_INDEX(msp_model,'-',-1)),''),'.jpg'), image_zoom = CONCAT('aap_product_images_zoom/aapcompare_',REPLACE(msp_model,CONCAT('-',SUBSTRING_INDEX(msp_model,'-',-1)),''),'_big.jpg' ),image_small = CONCAT('aap_product_images_small/aapcompare_',REPLACE(msp_model,CONCAT('-',SUBSTRING_INDEX(msp_model,'-',-1)),''),'_small.jpg') where resolved_url is null";
					params.add(parts[0]);
					params.add(parts[1]);
					upsertData(query, params);
					System.out.println("updated");
					params.clear();
				}

			}

			// updating the status i to null in msp_product_url table
			StringBuilder updateMspQuery = new StringBuilder("update msp_product_url set status = null where section in (");
			sections.forEach(s->{
				updateMspQuery.append("?,");
			});
			updateMspQuery.deleteCharAt(updateMspQuery.lastIndexOf(","));
			updateMspQuery.append(")");
			
			upsertData(updateMspQuery.toString(),sections);
			
			System.out.println(columnMappingMap.size());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
