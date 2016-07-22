package com.sourcecode.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



// this class contains all the urls from the msp site for each category

@Entity
@Table(name="category_main_url")
public class CategoryMainUrl {
    
    @Id
    private int no;
    private String section;
    
    @Column(name="first_page_url")
    private String  firstPageUrl;
    
    @Column(name="second_page_url")
    private String  secondPageUrl;
    
    @Column(name="total_pages")
    private int  totalPages;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getFirstPageUrl() {
        return firstPageUrl;
    }

    public void setFirstPageUrl(String firstPageUrl) {
        this.firstPageUrl = firstPageUrl;
    }

    public String getSecondPageUrl() {
        return secondPageUrl;
    }

    public void setSecondPageUrl(String secondPageUrl) {
        this.secondPageUrl = secondPageUrl;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
}
