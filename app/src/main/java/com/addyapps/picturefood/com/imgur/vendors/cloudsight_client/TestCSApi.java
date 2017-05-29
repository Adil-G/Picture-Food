package com.addyapps.picturefood.com.imgur.vendors.cloudsight_client;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class TestCSApi {

  private static final String API_KEY = "GO0_fhF_9foejWf-o0pmAg";

  static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  static final JsonFactory JSON_FACTORY = new JacksonFactory();



  public static void main(String[] args) throws Exception {
    getQueryFromImageURL("https://storage.googleapis.com/speech2text-149500.appspot.com/chocolate_filledd_muffins.PNG");
  }
  public static String getQueryFromImageURL(String url) throws IOException, InterruptedException, JSONException {

    CSApi api = new CSApi(
            HTTP_TRANSPORT,
            JSON_FACTORY,
            API_KEY
    );
    CSPostConfig imageToPost = CSPostConfig.newBuilder()
            .withRemoteImageUrl(url)
            .build();

    CSPostResult portResult = api.postImage(imageToPost);

    System.out.println("Post result: " + portResult);

    //System.out.println(api.getImage(portResult).getStatus());

    while (api.getImage(portResult).getStatus().equals("not completed")) {
      Thread.sleep(500);
    }

    CSGetResult scoredResult = api.getImage(portResult);
    String resultJSON =  scoredResult.toPrettyString();
    System.out.println(resultJSON);
    String query = new JSONObject(resultJSON).getString("name");

    System.out.println(query);
    return query;
  }
  public static String getQueryFromImageURL(File url) throws IOException, InterruptedException, JSONException {

    CSApi api = new CSApi(
            HTTP_TRANSPORT,
            JSON_FACTORY,
            API_KEY
    );
    CSPostConfig imageToPost = CSPostConfig.newBuilder()
            .withImage(url)
            .build();

    CSPostResult portResult = api.postImage(imageToPost);

    System.out.println("Post result: " + portResult);

    //System.out.println(api.getImage(portResult).getStatus());

    while (api.getImage(portResult).getStatus().equals("not completed")) {
      Thread.sleep(500);
    }

    CSGetResult scoredResult = api.getImage(portResult);
    String resultJSON =  scoredResult.toPrettyString();
    System.out.println(resultJSON);
    String query = new JSONObject(resultJSON).getString("name");

    System.out.println(query);
    return query;
  }
}