package com.sourcecode.standalone;

import java.sql.Connection;
import java.sql.ResultSet;

public class PriceUpdater {
	 static String host_prd = "jdbc:mysql://209.99.16.94:3306/aapcow9a_dbaapcompare9";
	 static String userName_prd = "aapcow9a_adbuser";
	 static String password_prd = "Admin@1234$";



	 static String host = "jdbc:mysql://localhost:3306/aapcompare";
	 static String userName = "root";
	 static String password = "";
	 static Connection con;
	 static ResultSet rs;
}
