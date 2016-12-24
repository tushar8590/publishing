package com.sourcecode.fkloadproductupdatedata;
/***
 * The class to parse JSON data.
 * Please refer to the instructions.txt
 *
 * @author vijay.v@flipkart.com
 * @version 1.0
 * Copyright (c) Flipkart India Pvt. Ltd.
 */

import java.lang.*;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class JSONDataParser extends DataParser {

    private String affiliateId;
    private String affiliateToken;
    private String affiliateBaseUrl;
    private Map<String, String> productDirectory;

    JSONDataParser(String affiliateId, String affiliateToken) {
        this.affiliateId = affiliateId;
        this.affiliateToken = affiliateToken;
        this.affiliateBaseUrl = "https://affiliate-api.flipkart.net/affiliate/api/" + affiliateId + ".json";
        //this.affiliateBaseUrl = "https://affiliate-api.flipkart.net/affiliate/api/deltaFeeds/"+affiliateId+"/category/mobiles/fromVersion/0.1.json";

        productDirectory = new HashMap<String, String>();
    }

    /***
     *  It gets the API Directory information from the API service and stores it locally.
     * @return true if initialization is successful.
     * @throws AffiliateAPIException
     */
    public boolean initializeProductDirectory() throws AffiliateAPIException {
        boolean return_value = true;
        try {
            // Query the API service and get back the result.
            String jsonData = queryService(affiliateBaseUrl);

            // Bookkeep the retrieved data in a local productDirectory Map.
            JSONObject obj = new JSONObject(jsonData);
            JSONObject listing = obj.getJSONObject("apiGroups").getJSONObject("affiliate").getJSONObject("apiListings");
            Iterator keys = listing.keys();
            while(keys.hasNext()) {

                String category_name = (String)keys.next();
                JSONObject variants = listing.getJSONObject(category_name).getJSONObject("availableVariants");

                // Sort the variants and get the latest version
                Iterator v_iterator = variants.keys();
                List<String> variant_keys = new ArrayList<String>();
                while(v_iterator.hasNext()) {
                    variant_keys.add((String)v_iterator.next());
                }
                Collections.sort(variant_keys);

                String category_url = variants.getJSONObject(variant_keys.get(0)).getString("get");

                productDirectory.put(category_name, category_url);
            }
        }
        catch(JSONException je) {
            return_value = false;
        }

        return return_value;
    }

    /***
     *
     * @return the locally stored product directory information (A list of categories and the corresponding URLs).
     * Originally updated using initializeProductDirectory() and it should be updated again if the URLs are expired.
     */
    public Map<String, String> getProductDirectory() {
        return productDirectory;
    }

    /***
     *
     * @param category
     * @return list of products for the given categery from the API service.
     * @throws AffiliateAPIException
     */
    public List<ProductInfo> getProductList(String category) throws AffiliateAPIException {

        List<ProductInfo> plist = new ArrayList<ProductInfo>();

        try {
            String queryUrl = getProductDirectory().get(category);

            while(queryUrl != null && !queryUrl.isEmpty()) {
                String jsonData = queryService(queryUrl);

                JSONObject obj = new JSONObject(jsonData);
                JSONArray productArray = obj.getJSONArray("productInfoList");

                for(int i =0; i < productArray.length(); i++) {

                    ProductInfo pinfo = new ProductInfo();
                    JSONObject inner_obj = productArray.getJSONObject(i).getJSONObject("productBaseInfo");
                    pinfo.setId(inner_obj.getJSONObject("productIdentifier").getString("productId"));

                    JSONObject attributes = inner_obj.getJSONObject("productAttributes");
                    pinfo.setTitle(attributes.getString("title"));
                    pinfo.setDescription(attributes.optString("productDescription", ""));
                    pinfo.setMrp(attributes.getJSONObject("maximumRetailPrice").getDouble("amount"));
                    pinfo.setSellingPrice(attributes.getJSONObject("sellingPrice").getDouble("amount"));
                    pinfo.setProductUrl(attributes.getString("productUrl"));
                    pinfo.setInStock(attributes.getBoolean("inStock"));

                    plist.add(pinfo);
                }

                // Fetch the products from the next URL. Here we set the limit to 500 products.
                queryUrl = obj.optString("nextUrl", "");
               // if(queryUrl != null && !queryUrl.isEmpty() && plist.size() > 5000) { queryUrl = ""; }
            }
        }
        catch(JSONException je) {

        }
        return plist;
    }
    public String getAffiliateId() {
        return affiliateId;
    }
    public String getAffiliateToken() {
        return affiliateToken;
    }
}