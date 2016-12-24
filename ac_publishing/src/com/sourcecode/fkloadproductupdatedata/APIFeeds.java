package com.sourcecode.fkloadproductupdatedata;
/***
 * The main class to execute.
 * Please refer to the instructions.txt
 *
 * @author vijay.v@flipkart.com
 * @version 1.0
 * Copyright (c) Flipkart India Pvt. Ltd.
 */

import java.lang.*;
import java.util.*;
import java.io.*;

public class APIFeeds {

	private DataParser parser;

	APIFeeds(String affiliateId, String affiliateToken, String downloadType) {
		if(downloadType.equalsIgnoreCase("XML")) {
			//            parser = new XMLDataParser(affiliateId, affiliateToken);
			System.out.println("Usage: APIFeeds <Affiliate ID> <Affiliate Token> <JSON>");

		}
		else {
			parser = new JSONDataParser(affiliateId, affiliateToken);
		}
	}

	public DataParser getParser() {
		return parser;
	}

	public static void main(String arguments[]) {

		
		
		
		/**
		 * Usage: APIFeeds <AffiliateID> <AffiliateToken> <XML/JSON>
		 */

		List<String> elecCat = new  ArrayList<String>();
		elecCat.add("televisions");
		elecCat.add("landline_phones");
		elecCat.add("tv_video_accessories");
		elecCat.add("software");
		elecCat.add("computer_storage");
		elecCat.add("network_components");
		elecCat.add("e_learning");
		elecCat.add("video_players");
		elecCat.add("mobiles");
		elecCat.add("air_coolers");
		elecCat.add("home_entertainment");
		elecCat.add("computer_components");
		elecCat.add("laptop_accessories");
		elecCat.add("mobile_accessories");
		elecCat.add("camera_accessories");
		elecCat.add("air_conditioners");
		elecCat.add("tablets");
		elecCat.add("refrigerator");
		elecCat.add("home_improvement_tools");
		elecCat.add("computer_peripherals");
		elecCat.add("cameras");
		elecCat.add("wearable_smart_devices");
		elecCat.add("audio_players");
		elecCat.add("tablet_accessories");
		elecCat.add("kitchen_appliances");
		elecCat.add("microwave_ovens");
		elecCat.add("laptops");
		elecCat.add("washing_machine");
		elecCat.add("gaming");
		elecCat.add("home_appliances");
		elecCat.add("desktops");


		String args[] = new String[3];
		args[0] = "a123pp9aa";
		args[1] ="3523e24e9a5047f7ab2c0dceef27a84c";
		args[2] ="JSON";
		if(args.length < 3) {
			System.out.println(); System.out.println();
			System.out.println("Usage: APIFeeds <Affiliate ID> <Affiliate Token> <JSON>");
			System.out.println(); System.out.println();
			return;
		}

		try {
			if (args[2].equalsIgnoreCase("XML")) {
				//            parser = new XMLDataParser(affiliateId, affiliateToken);
				System.out.println("Usage: APIFeeds <Affiliate ID> <Affiliate Token> <JSON>");
				return;
			}
			APIFeeds feeds = new APIFeeds(args[0], args[1], args[2]);

			// Query the API service to get the list of categories and the corresponding URLs and store it
			// locally in productDirectory Map.
			if(feeds.getParser().initializeProductDirectory()) {

				System.out.println("Choose one of the categories:");
				// Get the list of categories from the locally stored productDirectory Map.
				System.out.println(feeds.getParser().getProductDirectory().size());
				Iterator<String> category_iterator = feeds.getParser().getProductDirectory().keySet().iterator();

				Map tempMap = new HashMap();

				while(category_iterator.hasNext()) { 
					String singleCat = category_iterator.next();
					if(elecCat.contains(singleCat)){
						tempMap.put(singleCat,feeds.getParser().getProductDirectory().get(singleCat));
						//feeds.getParser().getProductDirectory().remove(singleCat);
					}

				}
				feeds.getParser().getProductDirectory().clear();
				feeds.getParser().getProductDirectory().putAll(tempMap);

				System.out.println(feeds.getParser().getProductDirectory().size());

				/*  System.out.print("Enter a category (or type 'q' to quit): ");
                Scanner s = new Scanner(System.in);
                String category;

                do {
                    category = s.nextLine();
                    if(category.equalsIgnoreCase("q")) { return; }

                    if(!feeds.getParser().getProductDirectory().keySet().contains(category)) {
                        System.out.print("Enter a valid category (or type 'q' to quit): ");
                    }
                    else {
                        break;
                    }
                } while(Boolean.TRUE);*/

				Iterator<String> category_data_iterator = feeds.getParser().getProductDirectory().keySet().iterator();

				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(new File("C:\\fk_sql\\fkpid_price_data.sql")));

					while(category_data_iterator.hasNext()) { 
						String cat = category_data_iterator.next();


						int count = 0;
						// Get a list of products for the given category.
						Iterator<ProductInfo>  products_iterator = feeds.getParser().getProductList(cat).listIterator();
						while(products_iterator.hasNext()) {
							ProductInfo product = products_iterator.next();
							// if(product.isInStock()) {
							// Some of the fields are printed.
							
							String url = product.getProductUrl();
							String fkId = url.substring(url.lastIndexOf("?pid=")+5,url.lastIndexOf("&affid=a123pp9aa"));
							//System.out.println("Title: " + product.getTitle());
							//System.out.println("URL: " + product.getProductUrl());
							//System.out.println("Price: " + product.getMrp() + "\n\n");
							//System.out.println("Price: " + product.getSellingPrice() + "\n\n");

							count++;
							// }
							 String str = "insert into  flipkart_price_update (fk_product_id, section, fk_sellingprice) values ('"+  fkId +"','"+cat+"','"+ product.getSellingPrice()+"');";
							
				                 writer.write(str);
				                 writer.write("\r\n");
						}

						System.out.println("Found " + count + " products in " + cat + " category.\n\n");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				System.out.println("Unable to contact the Flipkart Affiliate API service.");
			}
		}
		catch(AffiliateAPIException e) {
			System.out.println("API Exception raised: " + e.getMessage());
		}
	}
}

