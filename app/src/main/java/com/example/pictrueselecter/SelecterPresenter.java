package com.example.pictrueselecter;

import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SelecterPresenter {

    SelecterActivity activity;

    public SelecterPresenter(SelecterActivity mainActivity) {
        activity = mainActivity;
    }

    public void readPicture() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Media> mediaList = new ArrayList<>();
                Cursor cursorImage = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
                Cursor cursorVideo = activity.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
                getBasicInfo(mediaList, cursorImage);
                getBasicInfo(mediaList, cursorVideo);
                //配置经纬度
                setLocationFromExif(mediaList);
                //日期排序
                Collections.sort(mediaList);
                //添加日期分类
                activity.setMediaList(addDateData(mediaList));
            }
        }).start();
    }

    private void setLocationFromExif(List<Media> mediaList) {
        for (Media media : mediaList) {
            try {
                File file = new File(media.path);
                ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
                String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String TAG_DATETIME = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                media.hasLocation = longitude != null && latitude != null;
                long t1 = System.currentTimeMillis();
                media.uri = Uri.fromFile(file);
                long t2 = System.currentTimeMillis();
                Log.i("TAG", "uri耗时: " + (t2 - t1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Media> addDateData(List<Media> mediaList) {
        List<Media> addDataMediaList = new ArrayList<>();
        String lastDate = "";
        for (Media media : mediaList) {
            if (!media.date.equals(lastDate)) {
                addDataMediaList.add(new Media(media.date, false, true));
                lastDate = media.date;
            }
            addDataMediaList.add(media);
        }
        return addDataMediaList;
    }

    private void getBasicInfo(List<Media> mediaList, @NonNull Cursor cursorImage) {
        while (cursorImage.moveToNext()) {
            //获取图片名称
            String name = cursorImage.getString(cursorImage.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            String time = cursorImage.getString(cursorImage.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
            // 获取图片绝对路径
            int column_index = cursorImage.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursorImage.getString(column_index);
            mediaList.add(new Media(path, name, time, getDate(time), false, false));
            Log.i("extracted", "extracted: name = " + name + "  path = " + path + ".time.." + getDate(time));
        }
    }

    private String getDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long lt = new Long(s + "000");
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
}
