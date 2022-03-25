package com.example.pictrueselecter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    public static int width;
    public static int height;
    public static ExecutorService cachedThreadPool = Executors.newFixedThreadPool(8);

    public static void loadImageView(ImageView imageView, String path) {
        if (path.endsWith(".mp4")) {
            return;
        }
        if (width == 0 && height == 0) {
            imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    width = imageView.getMeasuredWidth();
                    height = imageView.getMeasuredHeight();
                    getBitmap(path, width, height);
                }
            });
        } else {
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = getBitmap2(path);

                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }
            });
        }
    }

    public static Bitmap getBitmap2(String path) {
        return BitmapFactory.decodeFile(path);
    }

    public static Bitmap getBitmap(String path, int width, int height) {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path, op); //获取尺寸信息
        //获取比例大小
        int wRatio = (int) Math.ceil(op.outWidth / width);
        int hRatio = (int) Math.ceil(op.outHeight / height);
        //如果超出指定大小，则缩小相应的比例
        if (wRatio > 1 && hRatio > 1) {
            if (wRatio > hRatio) {
                op.inSampleSize = wRatio;
            } else {
                op.inSampleSize = hRatio;
            }
        }
        op.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(path, op);
        return bmp;
    }
}
