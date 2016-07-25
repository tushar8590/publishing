package com.sourcecode.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="msp_electronics")
public class MspElectronics {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int sno;
    
    @Column(name = "product_id")
    private String productId;
    
    
    
    private String url;
    
    private String cod;
    
    private String image;
    
    private String price;
    private String rating;
    @Column(name = "model")
    private String model;
    
    @Column(name = "msp_model")
    private String mspModel;
    
    private String brand;
    

    
    private String website;
    private String offer;
    
    @Column(name = "image_zoom")
    private String imageZoom;
    
    @Column(name = "image_small")
    private String imageSmall;
    
    @Column(name = "image_msp")
    private String imageMsp;
    
    @Column(name = "latest_temp_prices")
    private String latestTempPrices;
    
    private String xpath;
    
    @Column(name = "emi_avaliable")
    private String emiAvaliable;
    
    @Column(name = "delivery_time")
    private String deliveryTime;
    
    @Column(name = "menu_level1")
    private String menuLevel1;
    
    @Column(name = "menu_level2")
    private String menuLevel2;
    
    private String section;
    
    @Column(name = "temp_flag")
    private String tempFlag;
    

    
  

    public int getSno() {
        return sno;
    }

    public void setSno(int sno) {
        this.sno = sno;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getImageZoom() {
        return imageZoom;
    }

    public void setImageZoom(String imageZoom) {
        this.imageZoom = imageZoom;
    }

    public String getImageSmall() {
        return imageSmall;
    }

    public void setImageSmall(String imageSmall) {
        this.imageSmall = imageSmall;
    }

    public String getImageMsp() {
        return imageMsp;
    }

    public void setImageMsp(String imageMsp) {
        this.imageMsp = imageMsp;
    }

    public String getLatestTempPrices() {
        return latestTempPrices;
    }

    public void setLatestTempPrices(String latestTempPrices) {
        this.latestTempPrices = latestTempPrices;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getEmiAvaliable() {
        return emiAvaliable;
    }

    public void setEmiAvaliable(String emiAvaliable) {
        this.emiAvaliable = emiAvaliable;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getMenuLevel1() {
        return menuLevel1;
    }

    public void setMenuLevel1(String menuLevel1) {
        this.menuLevel1 = menuLevel1;
    }

    public String getMenuLevel2() {
        return menuLevel2;
    }

    public void setMenuLevel2(String menuLevel2) {
        this.menuLevel2 = menuLevel2;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getTempFlag() {
        return tempFlag;
    }

    public void setTempFlag(String tempFlag) {
        this.tempFlag = tempFlag;
    }

    public String getMspModel() {
        return mspModel;
    }

    public void setMspModel(String mspModel) {
        this.mspModel = mspModel;
    }





}
