package com.adupp.administrator.fawazmoviedb;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Administrator on 1/15/2016.
 */
public class GridViewAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<String> mGridData = new ArrayList<String>();


    public GridViewAdapter(Context mContext, int layoutResourceId, ArrayList<String> mGridData)
    {
        super(mContext,layoutResourceId,mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }

    public void setGridData(ArrayList<String> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ImageView holder;
        if (row == null)
        {
            LayoutInflater inflater= ((Activity)mContext).getLayoutInflater();
            row=inflater.inflate(layoutResourceId,parent,false);
            holder =  (ImageView) row.findViewById(R.id.movieImageView);
        }
            else
        {
            holder =  (ImageView) row.findViewById(R.id.movieImageView);
        }

        //String item = "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg" ;
        String item = mGridData.get(position);
        Picasso.with(mContext).load(item).into(holder);
  return row;
       // return super.getView(position, convertView, parent);
    }



}
