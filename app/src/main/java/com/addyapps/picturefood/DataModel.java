package com.addyapps.picturefood;

public class DataModel {

    String ReadyIn;
    String inXMins;
    String dish;
    String imageURL;
    String id;
    public DataModel(String id,String ReadyIn, String inXMins, String dish, String imageURL ) {
        this.ReadyIn =ReadyIn;
        this.inXMins =inXMins;
        this.dish =dish;
        this.imageURL =imageURL;
        this.id = id;
    }
    public String getId()
    {
        return this.id;
    }

    public String getReadyIn() {
        return ReadyIn;
    }

    public String getInXMins() {
        return inXMins;
    }

    public String getDish() {
        return dish;
    }

    public String getImageURL() {
        return imageURL;
    }

}