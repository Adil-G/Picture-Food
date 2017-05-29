package com.addyapps.picturefood;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.addyapps.picturefood.akiniyalocts.imgurapiexample.UploadTest;
import com.addyapps.picturefood.helper.FoodElement;
import com.addyapps.picturefood.helper.FunnyCrawler;
import com.addyapps.picturefood.helper.RecipeAPI;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ImageView selectedImage;
    TextView resultTextView;

    static final int REQUEST_GALLERY_IMAGE = 10;
    static final int REQUEST_CODE_PICK_ACCOUNT = 11;
    static final int REQUEST_ACCOUNT_AUTHORIZATION = 12;
    static final int REQUEST_PERMISSIONS = 13;
    static final int REQUEST_PERMISSIONS_CAMERA = 14;
    static final int REQUEST_PERMISSIONS_GALLERY = 15;
    static final int REQUEST_PERMISSIONS_BLANK = 0;
    static int mCurrentInputType = REQUEST_PERMISSIONS_GALLERY;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    public void doAction(int type)
    {
        if(type == REQUEST_PERMISSIONS_GALLERY)
        {
            Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, REQUEST_GALLERY_IMAGE);
        }
        else if(type == REQUEST_PERMISSIONS_CAMERA) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
        }
        else if(type == REQUEST_PERMISSIONS_BLANK)
        {
            // Do nothing.
        }
    }
    public String getFromGallery(Intent data)
    {
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQUEST_GALLERY_IMAGE) && resultCode == RESULT_OK && data != null) {

            /*
            try {
                InputStream iStream =   getContentResolver().openInputStream(data.getData());
                byte[] inputData = getBytes(iStream);
                galleryAddPic();
                new RetrieveFeedTask().execute(inputData);;
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            Uri returnUri = data.getData(); Bitmap bitmap;
            String filePath = null;
            //galleryAddPic();
            filePath = getFromGallery(data);
            //Safety check to prevent null pointer exception
            System.out.println("9k9f39s0ef: "+filePath);
            if (filePath == null || filePath.isEmpty()) return;
            final File chosenFile = new File(filePath);
            Picasso.with(getBaseContext())
                    .load(chosenFile)
                    .placeholder(R.drawable.ic_photo_library_black)
                    .fit()
                    .into(selectedImage);

            /*resultTextView.setText("Processing image! This will take 30 seconds...");
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try{

                        giveRecipes(chosenFile);


                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }
            };

            thread.start();
            */


            final String finalFilePath = filePath;
            resultTextView.setText("Processing image! This will take 30 seconds...");
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try{
                        final String imageURL = UploadTest.getLinkFromDirectory(finalFilePath);
                        System.out.println("3fj8w9: Upload Finished: "+ imageURL);
                        giveRecipesInOrderChunk(imageURL, chosenFile);

                    } catch (org.json.JSONException jsonexception)
                    {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                resultTextView.setText("This image is too dark for me to understand!");
                            }
                        });
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }

                }
            };

            thread.start();

        }else if ((requestCode == REQUEST_TAKE_PHOTO) && resultCode == RESULT_OK && data != null) {
            // dispatchTakePictureIntent();
            /*
            try {
                InputStream iStream =   getContentResolver().openInputStream(photo);
                byte[] inputData = getBytes(iStream);
                galleryAddPic();
                new RetrieveFeedTask().execute(inputData);;
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            String filePath = null;
            //galleryAddPic();
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                filePath=saveBitmap(photo, "test.png");//;getRealPathFromURI(returnUri);//DocumentHelper.getPath(this, returnUri);


            } catch (IOException e) {
                e.printStackTrace();
            }
            //Safety check to prevent null pointer exception
            System.out.println("9k9f39s0ef: "+filePath);
            //Safety check to prevent null pointer exception
            if (filePath == null || filePath.isEmpty()) return;
            final File chosenFile = new File(filePath);
            Picasso.with(getBaseContext())
                    .load(chosenFile)
                    .placeholder(R.drawable.ic_photo_library_black)
                    .fit()
                    .into(selectedImage);
            /*
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try{

                        giveRecipes(chosenFile);


                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }
            };

            thread.start();
            */

            final String finalFilePath = filePath;
            resultTextView.setText("Processing image! This will take 30 seconds...");
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try{
                        final String imageURL = UploadTest.getLinkFromDirectory(finalFilePath);
                        System.out.println("3fj8w9: Upload Finished: "+ imageURL);
                        //giveRecipes(imageURL,chosenFile);
                        giveRecipesInOrderChunk(imageURL, chosenFile);
                    } catch (org.json.JSONException jsonexception)
                    {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                resultTextView.setText("This image is too dark for me to understand!");
                            }
                        });
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }

                }
            };

            thread.start();


        }
    }
    public static String foodCaption = "";
    public String saveBitmap(Bitmap bitmap, String filename) throws IOException {
        //create a file to write bitmap data
        File f = new File(this.getCacheDir(), filename);
        f.createNewFile();

//Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return f.getAbsolutePath();
    }

   /* private void giveRecipes(File url) throws Exception {
        final ArrayList<DataModel> dataModels= new ArrayList<>();
        JSONArray recipeArray = FunnyCrawler.results(url);


        for (int recipeIndex = 0; recipeIndex < recipeArray.length(); recipeIndex++) {
            try {
                JSONObject resultInfo = recipeArray.getJSONObject(recipeIndex);
                String id = resultInfo.getString("id");
                String title = resultInfo.getString("title");
                String readyInMinutes= "--";
                String image=  "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6d/Good_Food_Display_-_NCI_Visuals_Online.jpg/1200px-Good_Food_Display_-_NCI_Visuals_Online.jpg";
                try{
                    readyInMinutes = resultInfo.getString("readyInMinutes");
                    image = "https://webknox.com/recipeImages/"+resultInfo.getString("image");
                }catch (Exception f)
                {
                    f.printStackTrace();
                }
                dataModels.add(new DataModel(id,"Ready ", "in " + readyInMinutes + " min.", title, image));
                /*JSONObject resultInfo = entity_list.getJSONObject(resIndex);
                String form = resultInfo.getString("form");
                String type= resultInfo.getJSONObject("sementity").getString("type");
                System.out.println("09j398fj: "+type);
                for(Map.Entry<String,String> entry : foodElement.caption2Image.entrySet())
                {
                    if(entry.getKey().contains(form))
                    {
                        url =entry.getValue();
                        break;
                    }
                }
                if (type.equals("Top") || type.toLowerCase().contains("food"))
                    dataModels.add(new DataModel(form, "in " + type + " min.", "", url));

            }catch (Exception nullpointer)
            {
                nullpointer.printStackTrace();
            }
        }
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                resultTextView.setText(foodCaption);
                CustomAdapter adapter= new CustomAdapter(dataModels,getApplicationContext());

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        DataModel dataModel=  dataModels.get(position);

                                    /*
                                    String sdf = TranslateMe.getCaptionsInEnglish(
                                            new ArrayList<String>(
                                                    Arrays.asList(new String[]{}))).get(0);


                        Snackbar.make(view, dataModel.getReadyIn()+"\n"+dataModel.getInXMins()+" API: "+dataModel.getDish(), Snackbar.LENGTH_LONG)
                                .setAction("No action", null).show();
                    }
                });
            }
        });
    }*/
    private void giveRecipes(String url) throws Exception {
        System.out.println("3298j93fj: image click response");
        final ArrayList<DataModel> dataModels= new ArrayList<>();
        FoodElement foodElement = FunnyCrawler.resultsNoCaption(url);
        ArrayList<String> listOfIdeas = foodElement.captions;

        JSONObject fromInternet = new JSONObject(listOfIdeas.get(0));
        String showRawJSON = fromInternet.toString();

        JSONArray entity_list = fromInternet.getJSONArray("entity_list");

        for (int resIndex = 0; resIndex < entity_list.length(); resIndex++) {
            try {
                JSONObject resultInfo = entity_list.getJSONObject(resIndex);
                String form = resultInfo.getString("form");
                String type= resultInfo.getJSONObject("sementity").getString("type");
                System.out.println("09j398fj: "+type);
                for(Map.Entry<String,String> entry : foodElement.caption2Image.entrySet())
                {
                    if(entry.getKey().contains(form))
                    {
                        url =entry.getValue();
                        break;
                    }
                }
                if (true||type.equals("Top") || type.toLowerCase().contains("food"))
                    dataModels.add(new DataModel("",form, "in " + type + " min.", "", url));
            }catch (Exception nullpointer)
            {
                nullpointer.printStackTrace();
            }
        }
        final String finalRetThis = foodElement.description;
        final String finalUrl = url;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                resultTextView.setText(finalRetThis);
                CustomAdapter adapter= new CustomAdapter(dataModels,getApplicationContext());

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        DataModel dataModel=  dataModels.get(position);

                                    /*
                                    String sdf = TranslateMe.getCaptionsInEnglish(
                                            new ArrayList<String>(
                                                    Arrays.asList(new String[]{}))).get(0);
                                     */

                        Snackbar.make(view, dataModel.getReadyIn()+"\n"+dataModel.getInXMins()+" API: "+dataModel.getDish(), Snackbar.LENGTH_LONG)
                                .setAction("No action", null).show();

                    }
                });
            }
        });
    }
    private void giveRecipesInOrderChunk(String url,  File file) throws Exception {

        final ArrayList<DataModel> dataModels= new ArrayList<>();
        FoodElement foodElement = FunnyCrawler.resultsNoCaptionInOrder(url);
        ArrayList<String> listOfIdeas = new ArrayList<>(foodElement.caption2Image.keySet());


        for (int resIndex = 0; resIndex < listOfIdeas.size(); resIndex++) {
            String caption = listOfIdeas.get(resIndex);

            dataModels.add(new DataModel("", caption, "in " + "" + " min.", "", foodElement.caption2Image.get(caption)));
        }
        final String finalRetThis = foodElement.description;
        final String finalUrl = url;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                resultTextView.setText(finalRetThis);
                CustomAdapter adapter= new CustomAdapter(dataModels,getApplicationContext());

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        final DataModel dataModel=  dataModels.get(position);

                                    /*
                                    String sdf = TranslateMe.getCaptionsInEnglish(
                                            new ArrayList<String>(
                                                    Arrays.asList(new String[]{}))).get(0);
                                     */

                        Snackbar.make(view, dataModel.getReadyIn()+"\n"+dataModel.getInXMins()+" API: "+dataModel.getDish(), Snackbar.LENGTH_LONG)
                                .setAction("No action", null).show();
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try{

                                    giveRecipesInOrder(dataModel.getImageURL());


                                } catch(Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        };

                        thread.start();
                    }
                });
            }
        });
    }

    private void giveRecipesInOrder(String url) throws Exception {

        final ArrayList<DataModel> dataModels= new ArrayList<>();
        FoodElement foodElement = FunnyCrawler.resultsNoCaptionInOrder(url);
        ArrayList<String> listOfIdeas = new ArrayList<>(foodElement.caption2Image.keySet());


        for (int resIndex = 0; resIndex < listOfIdeas.size(); resIndex++) {
            String caption = listOfIdeas.get(resIndex);
            ArrayList<String> labels = new RecipeAPI().filterTopics(caption);
            dataModels.add(new DataModel("", caption, "in " + "" + " min.", "", foodElement.caption2Image.get(caption)));

        }
        final String finalRetThis = foodElement.description;
        final String finalUrl = url;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                resultTextView.setText(finalRetThis);
                CustomAdapter adapter= new CustomAdapter(dataModels,getApplicationContext());

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        DataModel dataModel=  dataModels.get(position);

                                    /*
                                    String sdf = TranslateMe.getCaptionsInEnglish(
                                            new ArrayList<String>(
                                                    Arrays.asList(new String[]{}))).get(0);
                                     */

                        Snackbar.make(view, dataModel.getReadyIn()+"\n"+dataModel.getInXMins()+" API: "+dataModel.getDish(), Snackbar.LENGTH_LONG)
                                .setAction("No action", null).show();
                        /*Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try{

                                    giveRecipes(finalUrl);


                                } catch(Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        };

                        thread.start();*/
                    }
                });
            }
        });
    }

    private void giveRecipes(String url,  File file) throws Exception {

        final ArrayList<DataModel> dataModels= new ArrayList<>();
        FoodElement foodElement = FunnyCrawler.resultsNoCaption(url);
        ArrayList<String> listOfIdeas = foodElement.captions;

        JSONObject fromInternet = new JSONObject(listOfIdeas.get(0));
        String showRawJSON = fromInternet.toString();

        JSONArray entity_list = fromInternet.getJSONArray("entity_list");

        for (int resIndex = 0; resIndex < entity_list.length(); resIndex++) {
            try {
                JSONObject resultInfo = entity_list.getJSONObject(resIndex);
                String form = resultInfo.getString("form");
                String type= resultInfo.getJSONObject("sementity").getString("type");
                System.out.println("09j398fj: "+type);
                for(Map.Entry<String,String> entry : foodElement.caption2Image.entrySet())
                {
                    if(entry.getKey().contains(form))
                    {
                        url =entry.getValue();
                        break;
                    }
                }
                if (true||type.equals("Top") || type.toLowerCase().contains("food"))
                    dataModels.add(new DataModel("",form, "in " + type + " min.", "", url));
            }catch (Exception nullpointer)
            {
                nullpointer.printStackTrace();
            }
        }
        final String finalRetThis = foodElement.description;
        final String finalUrl = url;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                resultTextView.setText(finalRetThis);
                CustomAdapter adapter= new CustomAdapter(dataModels,getApplicationContext());

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        DataModel dataModel=  dataModels.get(position);

                                    /*
                                    String sdf = TranslateMe.getCaptionsInEnglish(
                                            new ArrayList<String>(
                                                    Arrays.asList(new String[]{}))).get(0);
                                     */

                        Snackbar.make(view, dataModel.getReadyIn()+"\n"+dataModel.getInXMins()+" API: "+dataModel.getDish(), Snackbar.LENGTH_LONG)
                                .setAction("No action", null).show();
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try{

                                    giveRecipes(finalUrl);


                                } catch(Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        };

                        thread.start();
                    }
                });
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_GALLERY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    doAction(mCurrentInputType);

                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied",
                            Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button selectImageButton = (Button) findViewById(R.id
                .select_image_button);
        Button takePic = (Button) findViewById(R.id
                .take_pic_button);
        selectedImage = (ImageView) findViewById(R.id.selected_image);
        resultTextView = (TextView) findViewById(R.id.result);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentInputType = REQUEST_PERMISSIONS_CAMERA;
                doAction(mCurrentInputType);
               /* ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.GET_ACCOUNTS},
                        REQUEST_PERMISSIONS_CAMERA);*/


            }
        });
        final Activity currentActivity = this;
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentInputType = REQUEST_PERMISSIONS_GALLERY;
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(currentActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(currentActivity,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS_GALLERY);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }else
                {
                    doAction(mCurrentInputType);
                }

                /*ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.GET_ACCOUNTS},
                        REQUEST_PERMISSIONS_GALLERY);*/

            }
        });
        listView=(ListView)findViewById(R.id.list);

        final ArrayList<DataModel> dataModels= new ArrayList<>();

        //dataModels.add(new DataModel("Apple Pie", "Android 1.0", "1",""));

        CustomAdapter adapter= new CustomAdapter(dataModels,getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModel dataModel=  dataModels.get(position);

                Snackbar.make(view, dataModel.getReadyIn()+"\n"+dataModel.getInXMins()+" API: "+dataModel.getDish(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
    }
}
