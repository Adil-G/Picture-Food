package com.addyapps.picturefood.helper;

/**
 * Created by corpi on 2017-05-22.
 */
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;

public class TranslateMe {


    public static void main(String[] args) {

        try {
            // See comments on
            //   https://developers.google.com/resources/api-libraries/documentation/translate/v2/java/latest/
            // on options to set
            Translate t = new Translate.Builder(
                    com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport()
                    , com.google.api.client.json.gson.GsonFactory.getDefaultInstance(), null)
                    //Need to update this to your App-Name
                    .setApplicationName("Stackoverflow-Example")
                    .build();
            Translate.Translations.List list = t.new Translations().list(
                    Arrays.asList(
                            //Pass in list of strings to be translated
                            "Hola Mundo",
                            "Cómo utilizar Google Translate desde Java"),
                    //Target language
                    "EN");
            //Set your API-Key from https://console.developers.google.com/
            list.setKey("AIzaSyASx0PEjdvwRZuTy-1OAZnX7PreEhPFfME");
            TranslationsListResponse response = list.execute();
            for(TranslationsResource tr : response.getTranslations()) {
                System.out.println(tr.getTranslatedText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<String> getCaptionsInEnglish(ArrayList<String> originalCaptions)
    {
        try {
            // See comments on
            //   https://developers.google.com/resources/api-libraries/documentation/translate/v2/java/latest/
            // on options to set
            Translate t = new Translate.Builder(
                    com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport()
                    , com.google.api.client.json.gson.GsonFactory.getDefaultInstance(), null)
                    //Need to update this to your App-Name
                    .setApplicationName("Stackoverflow-Example")
                    .build();
            Translate.Translations.List list = t.new Translations().list(
                    originalCaptions,
                    //Target language
                    "EN");
            //Set your API-Key from https://console.developers.google.com/
            list.setKey("AIzaSyASx0PEjdvwRZuTy-1OAZnX7PreEhPFfME");
            TranslationsListResponse response = list.execute();
            ArrayList<String> translatedList = new ArrayList<>();
            for(TranslationsResource tr : response.getTranslations()) {
                String translation= tr.getTranslatedText();
                translatedList.add(translation);
                System.out.println(translation);
            }
            return translatedList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}