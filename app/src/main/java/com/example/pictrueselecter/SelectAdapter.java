package com.example.pictrueselecter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int Type_Date = 822;
    private static final int Type_Item = 878;
    Context context;
    List<Media> mediaList;

    public SelectAdapter(Context context, List<Media> mediaList) {
        this.context = context;
        this.mediaList = mediaList;
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mediaList.get(position).isDate) {
            return Type_Date;
        } else {
            return Type_Item;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Type_Item) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_selecter, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_selecter_date, parent, false);
            return new DateViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == Type_Item) {
            Log.i("TAG", "onBindViewHolder: " + position);
            ImageView iv = ((ItemViewHolder) holder).iv;
            ImageView ivLocation = ((ItemViewHolder) holder).ivLocation;
            CheckBox cb = ((ItemViewHolder) holder).cb;
            cb.setChecked(mediaList.get(position).isSelected);
            String path = mediaList.get(position).path;
            if (path.endsWith("mp4")) {
                iv.setImageResource(R.mipmap.ic_launcher);
            } else {
                BitmapPool.getInstance().with(context).into(iv,path);
//                BitmapPool.getInstance(context.getApplicationContext()).measureImageView(iv);
//                iv.setImageBitmap(BitmapPool.getInstance(context.getApplicationContext()).getBitmap(path));
            }
            ivLocation.setVisibility(mediaList.get(position).hasLocation ? View.VISIBLE : View.INVISIBLE);
        } else {
            TextView tv = ((DateViewHolder) holder).tv;
            CheckBox cb = ((DateViewHolder) holder).cb;
            tv.setText(mediaList.get(position).date);
            cb.setChecked(mediaList.get(position).isSelected);
        }
    }

    public static final float DISPLAY_WIDTH = 1000;
    public static final float DISPLAY_HEIGHT = 1000;


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            if (getItemViewType(position) == Type_Item) {
                CheckBox cb = ((ItemViewHolder) holder).cb;
                cb.setChecked(mediaList.get(position).isSelected);
            } else {
                TextView tv = ((DateViewHolder) holder).tv;
                CheckBox cb = ((DateViewHolder) holder).cb;
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(position);
                }
            }
        });

        holder.itemView.findViewById(R.id.cb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(position);
                }
            }
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ImageView iv;
        private final ImageView ivLocation;
        private final CheckBox cb;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            ivLocation = itemView.findViewById(R.id.iv_location);
            cb = itemView.findViewById(R.id.cb);
        }
    }

    class DateViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv;
        private final CheckBox cb;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
            cb = itemView.findViewById(R.id.cb);
        }
    }

    interface OnItemClickListener {
        void OnItemClick(int position);
    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
