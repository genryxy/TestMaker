package com.example.testcreator.Model;

public class SelectingTestView
{
    private String pathToImage;
    private String name;
    private String creator;

    public SelectingTestView() {}

    public SelectingTestView(String name, String creator, String pathToImage)
    {
        this.name = name;
        this.creator = creator;
        this.pathToImage = pathToImage;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public String getPathToImage()
    {
        return pathToImage;
    }

    public void setPathToImage(String pathToImage)
    {
        this.pathToImage = pathToImage;
    }
}
