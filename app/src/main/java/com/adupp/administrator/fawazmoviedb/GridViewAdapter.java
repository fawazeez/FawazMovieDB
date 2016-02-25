package com.adupp.administrator.fawazmoviedb;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 1/15/2016.
 */
public class GridViewAdapter extends ArrayAdapter<Griditem> {

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<Griditem> mGridData = new ArrayList<Griditem>();


    public GridViewAdapter(Context mContext, int layoutResourceId, ArrayList<Griditem> mGridData)
    {
        super(mContext,layoutResourceId,mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }

    public void setGridData(ArrayList<Griditem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        if (row == null)
        {
            LayoutInflater inflater= ((Activity)mContext).getLayoutInflater();
            row=inflater.inflate(layoutResourceId,parent,false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        }
            else
        {
            holder =  (ViewHolder) row.getTag();
        }

        //String item = "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg" ;
        Griditem item = mGridData.get(position);
        holder.titleTextView.setText(item.getOriginal_title());
        Picasso.with(mContext).load(item.getPoster_path()).placeholder(R.mipmap.ic_launcher).error(R.drawable.connection_error).into(holder.imageView);
         return row;
       // return super.getView(position, convertView, parent);
    }

    static class ViewHolder {
        @Bind(R.id.movieNameTextView) TextView titleTextView;
        @Bind(R.id.movieImageView) ImageView imageView;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
