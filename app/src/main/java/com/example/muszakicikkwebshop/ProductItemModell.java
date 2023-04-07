package com.example.muszakicikkwebshop;

public class ProductItemModell {

    private String Name;
    private String Info;
    private String Price;
    private float StarNumber;
    private String ImageResource;

    public ProductItemModell(){}

    public ProductItemModell(String name, String info, String price, float starNumber, String imageResource) {
        this.Name = name;
        Info = info;
        Price = price;
        StarNumber = starNumber;
        ImageResource = imageResource;
    }

    public String getName() {
        return Name;
    }

    public String getInfo() {
        return Info;
    }

    public String getPrice() {
        return Price;
    }

    public float getStarNumber() {
        return StarNumber;
    }

    public String getImageResource() {
        return ImageResource;
    }
}
