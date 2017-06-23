package com.addyapps.picturefood.helper;

import android.content.res.Resources;
import android.util.Log;

import com.addyapps.picturefood.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by corpi on 2017-06-06.
 */
public class HPEHavenAPI {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String API_KEY = "937ff12f-21d7-4b57-8578-66b6e410ed90";//"a6daa228-beb7-4571-aa7b-e24805c3a7f6";
    public static void main(String[] args) throws IOException {
        String query = "Sweet banana balls | Home Cooked: Malaysia | Asian Food Channel ...";
        getListOfWords(query, null);
    }

    public static ArrayList<String> getListOfWords(String query, Resources res) throws IOException {
        ArrayList<String> flagWords = new ArrayList<>();
        InputStream in_s = res.openRawResource(R.raw.exclude);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in_s));
        String line = reader.readLine();
        while(line != null){
            System.out.println(line);
            try{
                line = reader.readLine().toLowerCase();
                flagWords.add(line);
            }
            catch (Exception f){f.printStackTrace();break;}
        }
        reader.close();
        String filtered = filterQuery(query, flagWords);
        ArrayList<String> list  = extractQuery(filtered,flagWords);
        System.out.println("309j03j09f: "+filtered);
        return list;
    }
    public static  ArrayList<String> extractQuery(String query, ArrayList<String> flagWords) throws IOException {


        String name = query;
        name = URLEncoder.encode(name, "utf-8");
        String url = "https://api.havenondemand.com/1/api/sync/extractconcepts/v1?text="+name+"&apikey="+API_KEY;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept", "application/json");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        String result = response.toString();
        ArrayList<String> finalList = getKeysFromJSON(result);
        System.out.println(finalList);

        return finalList;
    }

    public static  String filterQuery(String query, ArrayList<String> flagWords) throws IOException {

        String name = query;
        name = URLEncoder.encode(name, "utf-8");
        String url = "https://api.havenondemand.com/1/api/sync/extractentities/v2?text="+name+"&entity_type=people_eng&entity_type=places_eng&entity_type=companies_eng&entity_type=compliance_eng&entity_type=drugs_eng&entity_type=films_eng&entity_type=holidays_eng&entity_type=languages_eng&entity_type=medical_conditions_eng&entity_type=organizations_eng&entity_type=professions_eng&entity_type=teams_eng&entity_type=universities_eng&entity_type=address_au&entity_type=address_ca&entity_type=address_de&entity_type=address_es&entity_type=address_fr&entity_type=address_gb&entity_type=address_it&entity_type=address_us&entity_type=address_zh&entity_type=person_fullname_eng&entity_type=person_name_component_eng&entity_type=pii&entity_type=pii_ext&entity_type=number_phone_au&entity_type=number_phone_ca&entity_type=number_phone_gb&entity_type=number_phone_us&entity_type=number_phone_de&entity_type=number_phone_fr&entity_type=number_phone_it&entity_type=number_phone_es&entity_type=number_phone_zh&entity_type=date_eng&entity_type=date_ger&entity_type=date_fre&entity_type=date_ita&entity_type=date_spa&entity_type=date_chi&entity_type=internet&entity_type=internet_email&entity_type=ip_address&entity_type=number_cc&entity_type=nationalinsurance_gb&entity_type=socialsecurity_us&entity_type=socialinsurance_ca&entity_type=licenseplate_us&entity_type=licenseplate_gb&entity_type=licenseplate_fr&entity_type=licenseplate_de&entity_type=licenseplate_ca&entity_type=driverslicense_us&entity_type=driverslicense_gb&entity_type=driverslicense_fr&entity_type=driverslicense_de&entity_type=driverslicense_ca&entity_type=bankaccount_ca&entity_type=bankaccount_fr&entity_type=bankaccount_gb&entity_type=bankaccount_ie&entity_type=bankaccount_us&entity_type=bankaccount_de&entity_type=file_hash&entity_type=organizations&entity_type=languages&entity_type=professions&entity_type=universities&entity_type=profanities&entity_type=films&entity_type=teams&entity_type=holidays&entity_type=medical_conditions&show_alternatives=false&apikey="+API_KEY;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept", "application/json");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        String result = response.toString();
        System.out.println(result);
        /*for(String word : getEntitiesFromJSON(result))
        {
            query = query.toLowerCase().replaceAll("[^\\d^\\w]+"+word.toLowerCase()+"[^\\d^\\w]+","");
        }*/
        BufferedReader reader;
        //final File file = new File("D:\\android-ndk-swig-example-master\\PictureFood\\app\\src\\main\\res\\raw\\exclude.txt");

       /* for(String word : getEntitiesFromJSON(result, flagWords))
        {
            query = query.toLowerCase().replaceAll(word.toLowerCase(),"");
        }*/
        for(String word : flagWords)
        {
            if(word.length()>2)
                query = query.toLowerCase().replaceAll(word.toLowerCase(),"");
        }
        System.out.println("309j03j09f-23:"+query+flagWords);

        return query;
    }
    public static ArrayList<String> getKeysFromJSON(String json)
    {
        ArrayList<String> entities = new ArrayList<>();
        try {
            JSONArray jsonObject =  new JSONObject(json).getJSONArray("concepts");
            for(int i =0;i<jsonObject.length();i++)
            {
                JSONObject info = jsonObject.getJSONObject(i);
                String word = info.getString("concept").toLowerCase();
                entities.add(word);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entities;
    }
    public static ArrayList<String> getEntitiesFromJSON(String json, ArrayList<String> flagWords)
    {
        ArrayList<String> entities = new ArrayList<>();
        try {
            JSONArray jsonObject =  new JSONObject(json).getJSONArray("entities");
            for(int i =0;i<jsonObject.length();i++)
            {
                JSONObject info = jsonObject.getJSONObject(i);
                String word = info.getString("normalized_text").toLowerCase();
                /*boolean wordIsFlagged = false;
                for(String wordX : flagWords)
                    if(!word.contains(wordX)) {
                        wordIsFlagged = true;


                    }
                if(wordIsFlagged)
                {

                }*/
                entities.add(word);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entities;
    }
}
