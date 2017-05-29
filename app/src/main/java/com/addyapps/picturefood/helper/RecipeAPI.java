package com.addyapps.picturefood.helper;


import android.net.Uri;

import com.addyapps.picturefood.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.ClarifaiPaginatedRequest;
import clarifai2.api.request.input.SearchClause;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.SearchHit;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.input.image.ClarifaiURLImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecipeAPI {

    private final String USER_AGENT = "Mozilla/5.0";
    public ClarifaiClient client;
    public Concept ferrari23;
    public RecipeAPI()
    {
        this.client = new ClarifaiBuilder("niT46CdlnqXAz_C2-VZSsV2hVwuQaObjUEXI6p5j", "vX918FvFKst2JdizK1blgudKcSvvp0jLlThAi0nz").buildSync();
        this.ferrari23 = Concept.forID("ferrari23");
    }
    public static ArrayList<String> getTagsFromImage(byte[] bytes) throws IOException {
        RecipeAPI res = new RecipeAPI();
        System.out.println();
        String urlX = "https://storage.googleapis.com/speech2text-149500.appspot.com/IMG_20170521_142159%5B1%5D.jpg";
        List<String> possible_tags =  res.getClarify2(bytes);
        HashSet<String> newUrls = new HashSet<>();
        HashMap<String, String> urlToName = new HashMap<>();
        for(String tag : possible_tags)
        {
            tag = tag.replaceAll("\\s","+");
            ArrayList<String> searchedImages = FunnyCrawler.bla(tag);
            for(String newUrl : searchedImages) {
                newUrls.add(newUrl);
                urlToName.put(newUrl, tag);
                res.client.addInputs()
                        .plus(
                                ClarifaiInput.forImage(ClarifaiImage.of(newUrl))
                        )
                        .executeSync();
            }
        }
        List<SearchHit> s= res.client.searchInputs(SearchClause.matchImageVisually(ClarifaiImage.of(urlX)))
                .getPage(1)
                .executeSync().get();
        ArrayList<String> properImages = new  ArrayList<>();
        ArrayList<String> properTags = new  ArrayList<>();
        for(SearchHit searchHit : s) {
            //if(newUrls.contains(searchHit.input().image().toString()))
            String url = ((ClarifaiURLImage)searchHit.input().image()).url().toString();
            if(newUrls.contains(url)) {
                System.out.println(urlToName.get(url));
                properImages.add(url);
                properTags.add(urlToName.get(url));
            }

        }
        System.out.println("result: "+properTags.get(0));
        return properTags;
    }
    public static ArrayList<String> getTagsFromImageURL(String urlImage, String caption) throws IOException {
        RecipeAPI res = new RecipeAPI();
        System.out.println();
        List<String> possible_tags =  res.getClarify2URL(urlImage);
        HashSet<String> newUrls = new HashSet<>();
        HashMap<String, String> urlToName = new HashMap<>();
        for(String tag : possible_tags)
        {
            if(tag.contains("\\s"))
                continue;
            tag += " "+caption;
            tag = tag.trim();
            System.out.println(tag);
            tag = tag.replaceAll("\\s","+");
            ArrayList<String> searchedImages = FunnyCrawler.bla(tag);
            for(String newUrl : searchedImages) {
                newUrls.add(newUrl);
                urlToName.put(newUrl, tag);
                res.client.addInputs()
                        .plus(
                                ClarifaiInput.forImage(ClarifaiImage.of(newUrl))
                        )
                        .executeSync();
            }
        }
        List<SearchHit> s= res.client.searchInputs(SearchClause.matchImageVisually(ClarifaiImage.of(urlImage)))
                .getPage(1)
                .executeSync().get();
        ArrayList<String> properImages = new  ArrayList<>();
        ArrayList<String> properTags = new  ArrayList<>();
        for(SearchHit searchHit : s) {
            //if(newUrls.contains(searchHit.input().image().toString()))
            String url = ((ClarifaiURLImage)searchHit.input().image()).url().toString();
            if(newUrls.contains(url)) {
                System.out.println(urlToName.get(url));
                properImages.add(url);
                properTags.add(urlToName.get(url));
            }

        }
        System.out.println("result: "+properTags.get(0));
        return properTags;
    }
    public static void main(String[] args) throws Exception {

        /*
        String key="AIzaSyCpYRDkENrnDuKDecGz4nN20GajOcW1HPA";
        String qry="Android";
        URL url = new URL(
                //"v1?searchType=image&key="+key+ "&cx=009861299925025910586:nu8l6aww458&q="+ qry + "&alt=json"
                "http://images.google.com/searchbyimage?image_url=http://i47.tinypic.com/ekepuq.png");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
            if(output.contains("\"link\": \"")){
                String link=output.substring(output.indexOf("\"link\": \"")+("\"link\": \"").length(), output.indexOf("\","));
                System.out.println(link);       //Will print the google search links
            }
        }
        conn.disconnect();*/

    }
/*
    public static void main(String[] args) throws Exception {

        String url = "https://api2.bigoven.com/Recipes?Title_kw=oysters&pg=1&rpp=20&api_key={your-api-key}";
        url = "http://www.themealdb.com/api/json/v1/1/search.php?s=butter";
        //https://webknox.com/recipeImages/
        //String data = new RecipeAPI().sendPost("");
        //new RecipeAPI().sendPost("");
       // System.out.println(new RecipeAPI().getClarify2());
    }*/
    public List<String> getClarify2(byte[] bytes)
    {
        List<ClarifaiOutput<Concept>> predictionResults =client.getDefaultModels().generalModel() // You can also do client.getModelByID("id") to get custom models
                 .predict()
                 .withInputs(
                         ClarifaiInput.forImage(ClarifaiImage.of(bytes))
                 )
                 .executeSync()
                 .get();
        List<String> resultList = new ArrayList<String>();
        if (predictionResults != null && predictionResults.size() > 0) {

            // Prediction List Iteration
            for (int i = 0; i < predictionResults.size(); i++) {

                ClarifaiOutput<Concept> clarifaiOutput = predictionResults.get(i);

                List<Concept> concepts = clarifaiOutput.data();

                if(concepts != null && concepts.size() > 0) {
                    for (int j = 0; j < concepts.size(); j++) {

                        resultList.add(concepts.get(j).name());
                    }
                }
            }
        }
        return resultList;
    }
    public List<String> getClarify2URL(String url)
    {
        List<ClarifaiOutput<Concept>> predictionResults =client.getDefaultModels().generalModel() // You can also do client.getModelByID("id") to get custom models
                .predict()
                .withInputs(
                        ClarifaiInput.forImage(ClarifaiImage.of(url))
                )
                .executeSync()
                .get();
        List<String> resultList = new ArrayList<String>();
        if (predictionResults != null && predictionResults.size() > 0) {

            // Prediction List Iteration
            for (int i = 0; i < predictionResults.size(); i++) {

                ClarifaiOutput<Concept> clarifaiOutput = predictionResults.get(i);

                List<Concept> concepts = clarifaiOutput.data();

                if(concepts != null && concepts.size() > 0) {
                    for (int j = 0; j < concepts.size(); j++) {

                        resultList.add(concepts.get(j).name());
                    }
                }
            }
        }
        return resultList;
    }
    public List<ClarifaiInput> getClarify(byte[] bytes)
    {
        return client.addInputs()
                .plus(
                        ClarifaiInput.forImage(ClarifaiImage.of(bytes))
                ).executeSync().get();
    }
/*
    public static String connect(String urlX) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        org.apache.http.HttpResponse response = httpclient.execute(new HttpGet(urlX));
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            String responseString = out.toString();
            out.close();
            return responseString;
            //..more logic
        } else {
            //Closes the connection.
            response.getEntity().getContent().close();
            throw new IOException(statusLine.getReasonPhrase());
        }

    }
*/
    private static String convertStreamToString(InputStream is) {
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    private static String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
    private String doit(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return readStream(in);
        } finally {
            urlConnection.disconnect();
        }
    }

    // HTTP GET request
    public String sendGet(String name) throws Exception {
        name = URLEncoder.encode(name, "utf-8");
        String url = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/search?number=10&query="+name;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("X-Mashape-Key", "5KhiX7Of0rmshyr1LFzQIlsxkfjTp1GM9wDjsnS1lVGz86RyJH");
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

        return result;
    }
    public JSONArray getRecipeExists(String name) throws Exception {
        name = URLEncoder.encode(name, "utf-8");;
        String url = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/autocomplete?number=10&query="+name;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("X-Mashape-Key", "5KhiX7Of0rmshyr1LFzQIlsxkfjTp1GM9wDjsnS1lVGz86RyJH");
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

        return new JSONArray(result);
    }

    public boolean recipeExists(String name) throws Exception {
        name = URLEncoder.encode(name, "utf-8");;
        String url = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/autocomplete?number=10&query="+name;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("X-Mashape-Key", "5KhiX7Of0rmshyr1LFzQIlsxkfjTp1GM9wDjsnS1lVGz86RyJH");
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

        return new JSONArray(result).length()!=0;
    }
    public String extractEntities(String name) throws Exception {
        name = URLEncoder.encode(name, "utf-8");;
        System.out.println("fe9s8jfw: "+name);
        String url = "https://topics-extraction.p.mashape.com/topics-2.0.php?dm=s&lang=en&of=json&rt=n&sdg=l&st=n&tt=a&txt="+name+"&txtf=plain&uw=n";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("X-Mashape-Key", "5KhiX7Of0rmshyr1LFzQIlsxkfjTp1GM9wDjsnS1lVGz86RyJH");
        con.setRequestProperty("accept", "application/json");

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

        return result;
    }
    public ArrayList<String> filterTopics(String captions) throws Exception {
        String foodElement = extractEntities(captions);
        final ArrayList<String> dataModels= new ArrayList<>();
        JSONObject fromInternet = new JSONObject(foodElement);
        String showRawJSON = fromInternet.toString();

        JSONArray entity_list = fromInternet.getJSONArray("entity_list");

        for (int resIndex = 0; resIndex < entity_list.length(); resIndex++) {
            try {
                JSONObject resultInfo = entity_list.getJSONObject(resIndex);
                String form = resultInfo.getString("form");
                String type= resultInfo.getJSONObject("sementity").getString("type");
                System.out.println("09j398fj: "+type);
                if (type.equals("Top") || type.toLowerCase().contains("food"))
                    dataModels.add(form);
            }catch (Exception nullpointer)
            {
                nullpointer.printStackTrace();
            }
        }
        return  dataModels;
    }

    public String sendPost3(String url) throws IOException, JSONException, URISyntaxException {
        String urlls = "https://yandex.com/images/search?url="+url+"&rpt=imageview";
        String newUrl = "http://www.google.com/searchbyimage?hl=en&image_url="+url;
        newUrl = urlls;
        Document doc = Jsoup.connect(newUrl).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.32 (KHTML, like Gecko) Chrome/32.0.1700.23 Safari/537.32").get();
        ArrayList<String> imgURLs=  new ArrayList<>();
        ArrayList<String> imgCaptions=  new ArrayList<>();
        for(int i = 0;i<10;i++) {
            try {
                Element img = doc.getElementsByClass("other-sites__thumb").get(i);
                //System.out.println(doc.html());
                String imgURL = img.attr("src");
                String caption = doc.getElementsByClass("other-sites__snippet").get(i).text();
                imgURLs.add(imgURL);
                imgCaptions.add(caption.replaceAll("http.*?\\s"," ").replaceAll("www.*?\\s"," "));
            }catch (Exception e){//e.printStackTrace();
            }
        }
        System.out.println("fiuen3w: "+imgCaptions);
        imgCaptions = TranslateMe.getCaptionsInEnglish(imgCaptions);
        //doc = Jsoup.connect("")
         //       .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36").get();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.havenondemand.com/1/api/sync/extractconcepts/v1?apikey=9a08df3d-7c28-4624-9038-2c45a9e7e416&text="+imgCaptions.toString())
                .get()
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        System.out.println(response.body().string());
        return response.body().string();







        //Element bestGuessElement = doc.getElementsByClass("iu-card-header").first();
       /* doc = Jsoup.connect("http://www.google.com"+bestGuessElement.attr("href")).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36").get();
        Element div = doc.getElementsByClass("rg_meta").first();

        String jsonString = div.text();
        //JSONObject root = new JSONObject(jsonString);
        //String result = root.getString("ou");
        System.out.println("result:"+jsonString);
        */
    }
    /*
    public ArrayList<String> sendPost4(String url, ArrayList<String> allTags, String caption, int level) throws IOException, JSONException {
        caption = allTags.get(level);


        String up = "http://images.google.com/searchbyimage?image_url="+url+"&query="+caption ;
        Document doc = Jsoup.connect(up).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36").get();
        System.out.println(doc.html());
        Elements elements = doc.getElementsByClass("rg_meta");
        ArrayList<String> all = new ArrayList<>();
        for(Element div : elements) {

            String jsonString = div.text();
            Pattern p = Pattern.compile("\"ou\":\"(.*?)\"");
            Matcher m = p.matcher(jsonString);
            if(all.size() == 0)
            {
                if(m.find()) {
                    //all.add(m.group(1));
                    all.addAll(sendPost4_Part2(m.group(1)));
                }
            }

            p = Pattern.compile("\"pt\":\"(.*?)\"");
            m = p.matcher(jsonString);
            if(m.find())
                all.add(m.group(1));

        }
        if(all.size()<=3)
            try {
                return sendPost4(url, allTags, "", level + 1);
            }catch (IndexOutOfBoundsException e)
            {
                return all;
            }
        return all;
    }
    */
    public HashMap<String, String> sendPost4(String url, ArrayList<String> allTags, String caption, int level) throws IOException, JSONException {
        caption = allTags.get(level);
        System.out.println("3k9fs09kef: "+caption);

        String up = "http://images.google.com/searchbyimage?image_url="+url+"&query="+caption+"&safe=active" ;
        Document doc = Jsoup.connect(up).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36").get();
        //System.out.println(doc.html());
        Elements elements = doc.getElementsByClass("rg_meta");
        Elements texts = doc.getElementsByClass("st");
        HashMap<String,String> all = new HashMap<>();
        int i = 0;
        for(Element div : elements) {

            String jsonString = div.text();
            Pattern p = Pattern.compile("\"ou\":\"(.*?)\"");
            Matcher m = p.matcher(jsonString);
            String curUrl  = url;
            String curCaption  = "";
            if(m.find())
                curUrl = m.group(1);
            if(all.size() == 0)
            {
                if(m.find()) {
                    // all.add(m.group(1));
                    all.putAll(sendPost4_Part2(m.group(1)));
                }
            }

            p = Pattern.compile("\"pt\":\"(.*?)\"");
            m = p.matcher(jsonString);
            if(m.find()) {
                try {
                    curCaption = texts.get(i).text();
                }catch (Exception indexOutOfBounds)
                {
                    indexOutOfBounds.printStackTrace();
                    curCaption = m.group(1);
                }
                all.put(curCaption, curUrl);
                // all.add(m.group(1));
            }
            i++;
        }
        if(all.size()<=3)
            try {
                return sendPost4(url, allTags, "", level + 1);
            }catch (IndexOutOfBoundsException e)
            {
                return all;
            }
        return all;
    }
    public HashMap<String, String> sendPost5(String url, ArrayList<String> allTags, String caption, int level) throws IOException, JSONException {
        caption = allTags.get(level);


        String up = "http://images.google.com/searchbyimage?image_url="+url+"&safe=active" ;
        Document doc = Jsoup.connect(up).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36").get();
        //System.out.println(doc.html());
        Elements elements = doc.getElementsByClass("rg_meta");
        //FoodElement all = new FoodElement();
        Elements texts = doc.getElementsByClass("st");
        HashMap<String,String> all = new HashMap<>();
        int i = 0;
        for(Element div : elements) {

            String jsonString = div.text();
            Pattern p = Pattern.compile("\"ou\":\"(.*?)\"");
            Matcher m = p.matcher(jsonString);
            String curUrl  = url;
            String curCaption  = "";
            if(m.find())
                curUrl = m.group(1);
            if(all.size() == 0)
            {
                if(m.find()) {
                   // all.add(m.group(1));
                    //all.putAll(sendPost4_Part2(m.group(1)));
                }
            }

            p = Pattern.compile("\"pt\":\"(.*?)\"");
            m = p.matcher(jsonString);
            if(m.find()) {
                try {
                    curCaption = texts.get(i).text();
                }catch (Exception indexOutOfBounds)
                {
                    indexOutOfBounds.printStackTrace();
                    curCaption = m.group(1);
                }
                all.put(curCaption, curUrl);
               // all.add(m.group(1));
            }
            i++;
        }
        return all;
    }

    public HashMap<String,String> sendPost4_Part2(String url) throws IOException, JSONException {
        if(true)
            return new HashMap<>();
        String up = " http://images.google.com/searchbyimage?image_url="+url+"&safe=active" ;
        Document doc = Jsoup.connect(up).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36").get();
       // System.out.println(doc.html());
        //Elements elements = doc.getElementsByClass("st");


        Elements elements = doc.getElementsByClass("rg_meta");
        Elements texts = doc.getElementsByClass("st");
        //FoodElement all = new FoodElement();
        HashMap<String,String> all = new HashMap<>();
        int i = 0;
        for(Element div : elements) {

            String jsonString = div.text();
            Pattern p = Pattern.compile("\"ou\":\"(.*?)\"");
            Matcher m = p.matcher(jsonString);
            String curUrl  = url;
            String curCaption  = "";
            if(m.find())
                curUrl = m.group(1);


            p = Pattern.compile("\"pt\":\"(.*?)\"");
            m = p.matcher(jsonString);
            if(m.find()) {
                try {
                curCaption = texts.get(i).text();
            }catch (Exception indexOutOfBounds)
            {
                indexOutOfBounds.printStackTrace();
                curCaption = m.group(1);
            }
                all.put(curCaption, curUrl);
                // all.add(m.group(1));
            }
            i++;
        }
        return all;
    }

}