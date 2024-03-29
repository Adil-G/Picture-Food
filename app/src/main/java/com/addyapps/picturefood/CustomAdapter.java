package com.addyapps.picturefood;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.addyapps.picturefood.helper.LoaderImageView;
import com.addyapps.picturefood.helper.RecipeAPI;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by corpi on 2017-05-18.
 */
public class CustomAdapter extends ArrayAdapter<DataModel>{

    private ArrayList<DataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
        TextView txtVersion;
        LoaderImageView info;
    }

    public CustomAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    /*@Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        final DataModel dataModel=(DataModel)object;


        Snackbar.make(v, dataModel.getReadyIn()+"\n"+dataModel.getInXMins()+" API: "+dataModel.getDish(), Snackbar.LENGTH_LONG)
                .setAction("No action", null).show();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try{

                    //giveRecipesInOrderChunkP2(dataModel.getImageURL(),null);
                    String caption = dataModel.getReadyIn();
                    if(!caption.toLowerCase().contains("recipe"))
                        caption += " recipes ";
                    caption = caption.trim();

                    //if(!caption.toLowerCase().contains("food")&&!caption.toLowerCase().contains("cuisine"))
                    //giveRecipes2(caption);
                    String[] caps =  caption.split("\\||-");
                    boolean recipeFound = false;
                    String url = null;
                    for(int i=0;i<caps.length&&!recipeFound;i++) {
                        String[] colonSides =  caps[i].split(":");
                        String side = null;
                        if(colonSides.length==1)
                            side = colonSides[0];
                        else if(colonSides.length>2)
                            side = colonSides[1];
                        else
                            side = caption;

                        if(!side.toLowerCase().contains("recipe"))
                            side += " recipes ";
                        side = side.trim();
                        if(side.replace("recipes","").trim().split("\\s").length==1)
                            continue;
                        if (side.toLowerCase().contains("food"))
                            continue;
                        ArrayList<String> urls = new RecipeAPI().getGoogleResultsYummly(side);
                        if (urls.size() == 0)
                            continue;
                        url = urls.get(0);
                        recipeFound = true;

                    }
                    if(recipeFound) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        getContext().startActivity(browserIntent);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }

            }
        };

        thread.start();
        /*
        switch (v.getId())
        {
            case R.id.item_info:
                Snackbar.make(v, "Release date " +dataModel.getImageURL(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                Picasso.with(getContext())
                        .load(dataModel.getImageURL())
                        .placeholder(R.drawable.ic_photo_library_black)
                        .resize(500,500)
                        .into(((LoaderImageView)v).getImageView());

                break;
        }
    }
    */

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
            viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.dish);
            viewHolder.info = (LoaderImageView) convertView.findViewById(R.id.item_info);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getReadyIn());
        viewHolder.txtType.setText(dataModel.getInXMins());
        viewHolder.txtVersion.setText(dataModel.getDish());
        //viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        //viewHolder.info.setImageDrawable(dataModel.getImageURL());
        Picasso.with(getContext())
                .load(dataModel.getImageURL())
                .placeholder(R.drawable.ic_photo_library_black)
                .resize(500,500)
                .into(((LoaderImageView) viewHolder.info).getImageView());
        // Return the completed view to render on screen
        return convertView;
    }
}