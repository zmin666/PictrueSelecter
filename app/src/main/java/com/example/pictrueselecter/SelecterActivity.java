package com.example.pictrueselecter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SelecterActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL = 965;
    ImageView iv;
    RecyclerView rv;
    ProgressBar progressBar;
    SelecterPresenter selecterPresenter;
    List<Media> mediaList = new ArrayList<>();
    private SelectAdapter selectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = findViewById(R.id.iv);
        rv = findViewById(R.id.rv);
        progressBar = findViewById(R.id.progressBar);
        initRecycleView();

        selecterPresenter = new SelecterPresenter(this);
        requestPermissions();
    }

    private void initRecycleView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mediaList.get(position).isDate) {
                    return 3;
                } else {
                    return 1;
                }
            }
        });
        rv.setLayoutManager(gridLayoutManager);
        selectAdapter = new SelectAdapter(this, mediaList);
        selectAdapter.setOnItemClickListener(new SelectAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Media media = mediaList.get(position);
                boolean isSelected = mediaList.get(position).isSelected;
                if (media.isDate) {
                    updateDateState(media.date, !isSelected);
                } else {
                    mediaList.get(position).isSelected = !isSelected;
                    selectAdapter.notifyItemChanged(position, "update");

                    //选择一个条目, 导致当前整个日期状态一致了
                    boolean currentItemState = mediaList.get(position).isSelected;
                    String date = mediaList.get(position).date;
                    boolean allState = true;
                    for (Media media2 : mediaList) {
                        if (!media2.isDate && media2.date.equals(date) && allState && media2.isSelected != currentItemState) {
                            allState = false;
                        }
                    }
                    //更新一个日期所有选项
                    if (allState && currentItemState) {//全部被选中
                        updateDateState(date, true);
                    } else if (!allState) { //有一个未被选中, 就是整日期未选中
                        for (int i = 0; i < mediaList.size(); i++) {
                            Media media2 = mediaList.get(i);
                            if (media2.isDate && media2.date.equals(date) && media2.isSelected) {
                                media2.isSelected = false;
                                selectAdapter.notifyItemChanged(i, "update");
                            }
                        }
                    }
                }

            }
        });
        rv.setAdapter(selectAdapter);
    }

    /**
     * 更新一个日期下的条目选择状态
     * 有变更的项目才去刷新
     *
     * @param data
     * @param state
     */
    private void updateDateState(String data, boolean state) {
        int starIndex = -1;
        int endIndex = -1;
        for (int i = 0; i < mediaList.size(); i++) {
            Media media1 = mediaList.get(i);
            if (media1.date.equals(data)) {
                if (starIndex == -1) {
                    starIndex = i;
                }
                media1.isSelected = state;
            } else {
                if (starIndex != -1 && endIndex == -1) {
                    endIndex = i;
                }
            }
        }
        selectAdapter.notifyItemRangeChanged(starIndex, endIndex, "update");
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL);
        } else {
            selecterPresenter.readPicture();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_EXTERNAL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selecterPresenter.readPicture();
                } else {
                    Toast.makeText(this, "获取存储权限失败, 无法展示图库,请去程序设置页面配置权限", Toast.LENGTH_SHORT).show();
                }
            }
            default: {
            }
        }
    }

    public void setMediaList(List<Media> list) {
        mediaList.clear();
        mediaList.addAll(list);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                selectAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void finishSelected(View view) {
        ArrayList<Media> mediaTempList = new ArrayList<>();
        for (Media media : mediaList) {
            if (!media.isDate && media.isSelected) {
                mediaTempList.add(media);
            }
        }
        Intent intent = getIntent();
        intent.putParcelableArrayListExtra(HomeActivity.picture_data, mediaTempList);
        setResult(RESULT_OK, intent);
        finish();
    }
}