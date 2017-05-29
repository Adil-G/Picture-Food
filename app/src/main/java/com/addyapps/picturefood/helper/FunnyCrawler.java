package com.addyapps.picturefood.helper;

/**
 * Created by corpi on 2017-05-21.
 */
import android.content.ReceiverCallNotAllowedException;

import com.addyapps.picturefood.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import timber.log.Timber;

public class FunnyCrawler {

    private static Pattern patternDomainName;
    private Matcher matcher;
    private static final String DOMAIN_NAME_PATTERN
            = "([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}";

    static {
        patternDomainName = Pattern.compile(DOMAIN_NAME_PATTERN);
    }

    public static void main(String[] args) throws Exception {

        /*System.out.println(FunnyCrawler.results(
                new File("S:\\IMG_20170526_010021.jpg")));*/
        System.out.println(new RecipeAPI().filterTopics("Hi my name is adil"));
    }
    public static JSONArray results(File file) throws Exception {
        String caption = com.addyapps.picturefood.com.imgur.vendors.cloudsight_client.TestCSApi.getQueryFromImageURL(file);
        MainActivity.foodCaption = caption;
        return resultsJustRecepies(caption);
    }
    public static FoodElement results(String url,File file) throws Exception {
        String caption = com.addyapps.picturefood.com.imgur.vendors.cloudsight_client.TestCSApi.getQueryFromImageURL(url);
        return results(url, caption);
    }
    public static FoodElement results(String url) throws Exception {
        String caption = com.addyapps.picturefood.com.imgur.vendors.cloudsight_client.TestCSApi.getQueryFromImageURL(url);
        return results(url, caption);
    }
    public static JSONArray resultsJustRecepies(String caption) throws Exception
    {
        RecipeAPI recipeAPI = new RecipeAPI();

        ArrayList<String> existing = new ArrayList<>();
        String[] captions = caption.split("\\s");
        List<int[]> list = new ArrayList<>();
        int[] indecies = new int[captions.length];
        for(int i=0;i<captions.length;i++)
        {
            indecies[i] = i;
        }
        combinations(Math.min(captions.length, 3), indecies, list);
        HashSet<HashSet<String>> resultsSet = new HashSet<>();
        TreeMap<Integer,HashSet<String>> results = new TreeMap<>(Collections.reverseOrder());

        for(int[] s:list)
        {
            HashSet<String> subCaption = new HashSet<String>();
            //String subCaption = "";
            for(int index : s)
            {
                subCaption.add(captions[index]);
            }
            resultsSet.add(subCaption);

        }
        int level = 0;
        for(HashSet<String> wordSet: resultsSet)
        {
            String subCaption = "";
            for(String word:wordSet)
            {
                subCaption+=word + " ";
            }
            subCaption = subCaption.trim();

            int length = subCaption.length();
            if(results.containsKey(length))
            {
                HashSet<String> hashSet = results.get(length);
                hashSet.add(subCaption);
                results.put(length,hashSet);

            }
            else
            {
                HashSet<String> hashSet = new HashSet<String>();
                hashSet.add(subCaption);
                results.put(length,hashSet);
            }

            level++;
        }
        ArrayList<String> finalProduct = new ArrayList<>();
        int CatCount = 0;
        JSONArray arr = new JSONArray();
        for(Map.Entry<Integer,HashSet<String>> entry : results.entrySet())
        {
            boolean thisLevelIsEnough = false;
            for(String cap : entry.getValue())
            {
                if(recipeAPI.recipeExists(cap))
                {
                    thisLevelIsEnough = true;
                    finalProduct.add(cap);
                    JSONArray recipies = recipeAPI.getRecipeExists(cap);
                    for(int i = 0;i<recipies.length();i++)
                    {
                        arr.put(recipies.getJSONObject(i));
                    }

                }
            }
            if(thisLevelIsEnough)
            {
                CatCount++;

            }
            if(CatCount>1)
            {
                break;
            }
        }


        int count = 0;
        for(String product: finalProduct)
        {
            JSONArray recipies = new JSONObject(recipeAPI.sendGet(product)).getJSONArray("results");
            for(int i = 0;i<recipies.length();i++)
            {
                arr.put(recipies.getJSONObject(i));
            }

        }

        return arr;
    }
    public static void combinations(int n, int[] arr, List<int[]> list) {
        // Calculate the number of arrays we should create
        int numArrays = (int)Math.pow(arr.length, n);
        // Create each array
        for(int i = 0; i < numArrays; i++) {
            list.add(new int[n]);
        }
        // Fill up the arrays
        for(int j = 0; j < n; j++) {
            // This is the period with which this position changes, i.e.
            // a period of 5 means the value changes every 5th array
            int period = (int) Math.pow(arr.length, n - j - 1);
            for(int i = 0; i < numArrays; i++) {
                int[] current = list.get(i);
                // Get the correct item and set it
                int index = i / period % arr.length;
                current[j] = arr[index];
            }
        }
    }
    public static FoodElement results(String url, String caption) throws Exception
    {
        RecipeAPI recipeAPI = new RecipeAPI();

        //ArrayList<String> allCaptions = new ArrayList<>();
        try{
            //allCaptions.add(recipeAPI.getTagsFromImageURL(url, caption).get(0));
            //allCaptions.add(caption);
            /*
            for(String cap : allCaptions.get(0).split("\\s|\\+"))
            {
                if(!cap.equals(caption))
                    allCaptions.add(cap);
            }
            */
        }catch (Exception e){}

        //allCaptions.add("");
        HashMap<String,String> hi = new HashMap<>();
        int trialNumber = 0;
        String[] allCaptions = caption.toLowerCase().split("\\sand\\s|\\swith\\s|\\sor\\s|\\son\\s|\\sof\\s");
        TreeMap<Integer, HashSet<String>> tree =new TreeMap<>(Collections.reverseOrder());

        for(String cap : allCaptions)
        {
            int index = cap.length();
            if(tree.containsKey(index))
            {
                HashSet<String> hashSet = tree.get(index);
                hashSet.add(cap);
                tree.put(index,hashSet);

            }
            else
            {
                HashSet<String> hashSet =new HashSet<>();
                hashSet.add(cap);
                tree.put(index,hashSet);
            }

        }
        System.out.println("f3j89jw9j: "+allCaptions);
        ArrayList<String> allCapsInOrder = new ArrayList<>();

        for(Map.Entry<Integer,HashSet<String>> entry :tree.entrySet())
        {
            for(String cap : entry.getValue())
            {
                if(cap.replaceAll("\\s","").length() >= 3)
                    allCapsInOrder.add(cap);
            }
        }
        hi = recipeAPI.sendPost4(url
                ,new ArrayList<String>(Arrays.asList(new String[]{caption})), "",0);
        while(hi.size()==0&&trialNumber < allCapsInOrder.size())
        {

            String cap = allCapsInOrder.get(trialNumber);
            if(!recipeAPI.recipeExists(cap)) {
                trialNumber++;
                continue;
            }
            for(String capX : cap.split("\\s")) {
                hi.putAll(recipeAPI.sendPost4(url
                        , new ArrayList<String>(Arrays.asList(new String[]{capX})), "", 0))
                ;
            }
            trialNumber++;

        }
        if(hi.size()==0)
        {
            allCapsInOrder = new ArrayList<>(Arrays.asList(caption.split("\\s")));
            trialNumber = 0;
            while(trialNumber <3&&trialNumber < allCapsInOrder.size())
            {
                String cap = allCapsInOrder.get(trialNumber);
                if(!recipeAPI.recipeExists(cap)) {
                    trialNumber++;
                    continue;
                }
                hi.putAll(recipeAPI.sendPost4(url
                        ,new ArrayList<String>(Arrays.asList(new String[]{cap})), "",0));
                ;
                trialNumber++;

            }
        }
        System.out.println("f3sj9f8w9: "+hi);
        //String hi = recipeAPI.sendPost2(url);
        String rawData =hi.keySet().toString();
        ArrayList<String> sdf = new ArrayList<>();
        sdf.add(recipeAPI.extractEntities(rawData));
        FoodElement returnThis = new FoodElement(hi, sdf);
        returnThis.description = caption;
        return returnThis;
    }
    public static FoodElement resultsNoCaptionInOrder(String url) throws Exception
    {
        RecipeAPI recipeAPI = new RecipeAPI();

        //ArrayList<String> allCaptions = new ArrayList<>();

        HashMap<String,String> hi = new HashMap<>();
        hi = recipeAPI.sendPost5(url
                ,new ArrayList<String>(Arrays.asList(new String[]{""})), "",0);
        System.out.println("f3sj9f8w9: "+hi);
        //String hi = recipeAPI.sendPost2(url);
        String rawData =hi.keySet().toString();
        ArrayList<String> sdf = new ArrayList<>();
        sdf.add(recipeAPI.extractEntities(rawData));
        FoodElement returnThis = new FoodElement(hi, sdf);
        returnThis.description = "";
        return returnThis;
    }

    public static FoodElement resultsNoCaption(String url) throws Exception
    {
        RecipeAPI recipeAPI = new RecipeAPI();

        //ArrayList<String> allCaptions = new ArrayList<>();

        HashMap<String,String> hi = new HashMap<>();
        hi = recipeAPI.sendPost5(url
                ,new ArrayList<String>(Arrays.asList(new String[]{""})), "",0);
        System.out.println("f3sj9f8w9: "+hi);
        //String hi = recipeAPI.sendPost2(url);
        String rawData =hi.keySet().toString();
        ArrayList<String> sdf = new ArrayList<>();
        sdf.add(recipeAPI.extractEntities(rawData));
        FoodElement returnThis = new FoodElement(hi, sdf);
        returnThis.description = "";
        return returnThis;
    }


    public String getDomainName(String url){

        String domainName = "";
        matcher = patternDomainName.matcher(url);
        if (matcher.find()) {
            domainName = matcher.group(0).toLowerCase().trim();
        }
        return domainName;

    }

    private Set<String> getDataFromGoogle(String query) {

        Set<String> result = new HashSet<String>();
        String request = "http://images.google.com/searchbyimage?image_url=http://i47.tinypic.com/ekepuq.png";//"https://www.google.com/search?q=" + query + "&num=20";
        System.out.println("Sending request..." + request);

        try {

            // need http protocol, set this as a Google bot agent :)
            Document doc = Jsoup
                    .connect(request)
                    .userAgent(
                            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
                    .timeout(5000).get();
            System.out.println(doc.html());
            // get all links;
            Elements links = doc.select("a[href]");
            for (Element link : links) {

                String temp = link.attr("href");
                if(temp.startsWith("/url?q=")){
                    //use regex to get domain name
                    result.add(getDomainName(temp));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
    public static ArrayList<String> bla(String qry) throws IOException {
        String key = "AIzaSyDMYmHcLImPxxEbPtM6DZyD1CDaE67t4vk";

        URL url = new URL(
                "https://www.googleapis.com/customsearch/v1?searchType=image&key=" + key + "&cx=009861299925025910586:nu8l6aww458&q=" + qry + "&alt=json");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        ArrayList<String> imglinks = new ArrayList<>();
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {

            if (output.contains("\"link\": \"")) {
                String link = output.substring(output.indexOf("\"link\": \"") + ("\"link\": \"").length(), output.indexOf("\","));
                System.out.println(link);       //Will print the google search links
                imglinks.add(link);
            }
        }
        conn.disconnect();
        return new ArrayList<>(Arrays.asList(new String[]{imglinks.get(0)}));
    }
}