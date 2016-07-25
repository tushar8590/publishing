package com.sourcecode.spring.model;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;



@Entity
@Table(name="new_menu")
public class NewMenu {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int sno;
    
    private String category;
    
    @Column(name = "menu_level1")
    private String menuLevel1;
    
    @Column(name = "menu_level2")
    private String menuLevel2;
    
    private String section;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    private String href;
    
    @Column(name = "main_menu_image")
    private String mainMenuImage;
    
    private int includedtop;

    
    @Transient
    private String subModuleName;
    
    @Transient
    private String allCats;
    
    public String getAllCats() {
		return allCats;
	}

	public void setAllCats(String allCats) {
		this.allCats = allCats;
	}

	public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getMainMenuImage() {
        return mainMenuImage;
    }

    public void setMainMenuImage(String mainMenuImage) {
        this.mainMenuImage = mainMenuImage;
    }

    public int getIncludedtop() {
        return includedtop;
    }

    public void setIncludedtop(int includedtop) {
        this.includedtop = includedtop;
    }

    public String getSubModuleName() {
        return subModuleName;
    }

    public void setSubModuleName(String subModuleName) {
        this.subModuleName = subModuleName;
    }
    
    
    
}
