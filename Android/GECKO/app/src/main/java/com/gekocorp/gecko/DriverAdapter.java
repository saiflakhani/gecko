package com.gekocorp.gecko;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gekocorp.gecko.PojoForDriver;

import java.util.ArrayList;

public class DriverAdapter extends ArrayAdapter<PojoForDriver> {
    private Context c;

    public DriverAdapter(Context context, int resource,  ArrayList<PojoForDriver> objects) {
        super(context, resource, objects);
        c = context;
    }

    public PojoForDriver currentPojo;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PojoForDriver dataModel = getItem(position);
        final int posi = position;
        currentPojo = dataModel;
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.log_list_item, parent, false);
            viewHolder.dateView = (TextView) convertView.findViewById(R.id.tVdateTitle);
            viewHolder.location = (TextView) convertView.findViewById(R.id.tVlocation);
            viewHolder.btnMap = (Button) convertView.findViewById(R.id.btnMap);
            //viewHolder.btnDetails = (Button) convertView.findViewById(R.id.btnDetail);
            viewHolder.iVBackground = (ImageView)convertView.findViewById(R.id.imageView);
            viewHolder.name = (TextView)convertView.findViewById(R.id.tVName);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        //TODO PULL THESE

        viewHolder.dateView.setText(dataModel.getTripDate());//TODO PULL
        double avg = 0;
        avg = Double.parseDouble(dataModel.getAvgKmpl());
        viewHolder.location.setText("Average : "+String.format("%.2f", avg)+" kmpl");
        viewHolder.name.setText(dataModel.getName());
        viewHolder.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c,MapsActivity.class);
                i.putExtra("position",posi);
                c.startActivity(i);
            }
        });
        //viewHolder.iVBackground.setTag(position);
        Glide.with(c).load("https://source.unsplash.com/random/800x400")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(viewHolder.iVBackground);
        // Return the completed view to render on screen
        return convertView;
    }



    public class ViewHolder{
        public TextView dateView,location,duration,distance,name;
        public Button btnMap,btnDetails;
        public ImageView iVBackground;

    }
}