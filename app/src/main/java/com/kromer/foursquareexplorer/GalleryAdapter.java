package com.kromer.foursquareexplorer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    ArrayList<String> data;
    Context context;
    ImageLoaderConfiguration config;
    ImageLoader imgloader;


    public GalleryAdapter(Context context, ArrayList<String> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
        config = new ImageLoaderConfiguration.Builder(context).build();
        imgloader.getInstance().init(config);
        imgloader = ImageLoader.getInstance();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = inflater.inflate(R.layout.gallery_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {

        imgloader.displayImage(data.get(position),
                myViewHolder.imvIcon);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imvIcon;

        MyViewHolder(View itemView) {
            super(itemView);
            imvIcon = (ImageView) itemView.findViewById(R.id.imvPic);
        }


    }
}
