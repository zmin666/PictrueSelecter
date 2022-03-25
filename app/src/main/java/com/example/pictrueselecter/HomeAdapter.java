package com.example.pictrueselecter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ItemViewHolder> {

    Context context;
    List<Media> mediaList;

    public HomeAdapter(Context context, List<Media> mediaList) {
        this.context = context;
        this.mediaList = mediaList;
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ImageView iv = ((ItemViewHolder) holder).iv;
        ImageView ivLocation = ((ItemViewHolder) holder).ivLocation;
        String path = mediaList.get(position).path;
        if (path.endsWith("mp4")) {
            iv.setImageResource(R.mipmap.ic_launcher);
        } else {
            BitmapPool.getInstance().with(context).into(iv,path);
        }
        ivLocation.setVisibility(mediaList.get(position).hasLocation ? View.VISIBLE : View.INVISIBLE);
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ImageView iv;
        private final ImageView ivLocation;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            ivLocation = itemView.findViewById(R.id.iv_location);
        }
    }
}
