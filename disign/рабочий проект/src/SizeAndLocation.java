package com.company;

public class SizeAndLocation
{
    private int sizeX;
    private int sizeY;
    private int locationX;
    private int locationY;

    public SizeAndLocation(int sizeX, int sizeY, int locationX, int locationY)
    {
        this.locationX = locationX;
        this.locationY = locationY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public int getSizeX()
    {
        return sizeX;
    }

    public int getLocationX()
    {
        return locationX;
    }

    public int getLocationY()
    {
        return locationY;
    }

    public int getSizeY()
    {
        return sizeY;
    }

    public void setLocationX(int locationX)
    {
        this.locationX = locationX;
    }

    public void setLocationY(int locationY)
    {
        this.locationY = locationY;
    }

    public void setSizeX(int sizeX)
    {
        this.sizeX = sizeX;
    }

    public void setSizeY(int sizeY)
    {
        this.sizeY = sizeY;
    }
}
