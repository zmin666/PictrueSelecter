package com.example.pictrueselecter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final int request_code = 328;
    public static final String picture_data = "picture_data";

    RecyclerView rv;
    List<Media> mediaList = new ArrayList<>();
    private HomeAdapter homeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        rv = findViewById(R.id.rv);
        initRecycleView();
    }

    private void initRecycleView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rv.setLayoutManager(gridLayoutManager);
        homeAdapter = new HomeAdapter(this, mediaList);
        rv.setAdapter(homeAdapter);
    }

    public void clickToSelect(View view) {
        startActivityForResult(new Intent(this, SelecterActivity.class), request_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_code && resultCode == RESULT_OK) {
            mediaList.clear();
            ArrayList<Media> arrayList = data.getExtras().getParcelableArrayList(picture_data);
            mediaList.addAll(arrayList);
            homeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        BitmapPool.getInstance().onDestroy();
        super.onDestroy();
    }
}