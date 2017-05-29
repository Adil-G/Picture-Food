package com.addyapps.picturefood.helper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by corpi on 2017-05-25.
 */
public class FoodElement {
    public HashMap<String,String> caption2Image;
    public ArrayList<String> captions;
    public String description;
    public FoodElement()
    {
        this.captions =new ArrayList<String>();
        this.caption2Image = new HashMap<String,String>();
        this.description = new String();
    }
    public FoodElement(HashMap<String,String> caption2Image,ArrayList<String> captions)
    {
        this.captions =captions;
        this.caption2Image = caption2Image;
        this.description = new String();
    }
}
