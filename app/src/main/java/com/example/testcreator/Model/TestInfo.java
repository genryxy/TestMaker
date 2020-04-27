package com.example.testcreator.Model;

import java.util.Date;

public class TestInfo {
    private String name;
    private String info;
    private int categoryID;
    private String pathToImg;
    private String creator;
    private Date dateCreation;

    public TestInfo() {}

    public TestInfo(String name, int categoryID, String info, String pathToImg, String creator, Date dateCreation) {
        this.name = name;
        this.info = info;
        this.pathToImg = pathToImg;
        this.creator = creator;
        this.dateCreation = dateCreation;
        this.categoryID = categoryID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPathToImg() {
        return pathToImg;
    }

    public void setPathToImg(String pathToImg) {
        this.pathToImg = pathToImg;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }
}
