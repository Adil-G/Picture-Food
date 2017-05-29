package com.addyapps.picturefood.helper;

/**
 * Created by corpi on 2017-05-21.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HttpFileUpload {
    private static final String IMGUR_CLIENT_ID = "...";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");


    public static void main(String args[]) {
        try {
            /*
            HttpClient client = new DefaultHttpClient();
            String url="https://www.google.co.in/searchbyimage/upload";
            String imageFile="c:\\temp\\shirt.jpg";
            HttpPost post = new HttpPost(url);

            MultipartEntity entity = new MultipartEntity();
            entity.addPart("encoded_image", new FileBody(new File(imageFile)));
            entity.addPart("image_url",new StringBody(""));
            entity.addPart("image_content",new StringBody(""));
            entity.addPart("filename",new StringBody(""));
            entity.addPart("h1",new StringBody("en"));
            entity.addPart("bih",new StringBody("179"));
            entity.addPart("biw",new StringBody("1600"));

            post.setEntity(entity);
            HttpResponse response = client.execute(post);

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            while ((line = rd.readLine()) != null) {
                if (line.indexOf("HREF")>0)
                    System.out.println(line.substring(8));
            }

        }catch (ClientProtocolException cpx){
            cpx.printStackTrace();
        }catch (IOException ioex){
            ioex.printStackTrace();
        }*/
            // Use the imgur image upload API as documented at https://api.imgur.com/endpoints/imag
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("encoded_image", "IMG_20170519_234628.jpg",
                            RequestBody.create(MEDIA_TYPE_PNG, new File("C:\\Users\\corpi\\Desktop\\IMG_20170519_234628.jpg")))
                    .addFormDataPart("image_url", "")
                    .addFormDataPart("image_content", "")
                    .addFormDataPart("filename", "")
                    .addFormDataPart("h1", "en")
                    .addFormDataPart("bih", "179")
                    .addFormDataPart("biw", "1600")
                    .build();

            Request request = new Request.Builder()
                    .url("https://www.google.co.in/searchbyimage/upload")
                    .post(RequestBody.create(MEDIA_TYPE_PNG, new File("C:\\Users\\corpi\\Desktop\\IMG_20170519_234628.jpg")))
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
