package com.sourcecode.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name ="msp_product_url")
public class MspProductUrl {
    
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int sno;
    
    @Column(name = "product_id")
    private String productId;
    
    @Column(name = "model")
    private String model;
    
    private String url;
    
    @Column(name = "menu_level1")
    private String menuLevel1;
    
    @Column(name = "menu_level2")
    private String menuLevel2;
    
    private String section;
    
    @Column(name = "temp_flag")
    private String tempFlag;
    
    @Column(name = "product_spec")
    private String productSpec;
    
    @Column(name = "spec_url")
    private String specURL;
    
    @Column(name = "STATUS")
    private String status;

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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getProductSpec() {
        return productSpec;
    }

    public void setProductSpec(String productSpec) {
        this.productSpec = productSpec;
    }

    public String getSpecURL() {
        return specURL;
    }

    public void setSpecURL(String specURL) {
        this.specURL = specURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MspProductUrl other = (MspProductUrl) obj;
        if (url == null) {
            if (other.url != null)
                return false;
        }
        else if (!url.equals(other.url))
            return false;
        return true;
    }
    
}
