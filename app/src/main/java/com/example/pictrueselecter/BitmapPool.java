package com.example.pictrueselecter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;

import java.io.FileNotFoundException;


public class BitmapPool {
    static LruCache<String, Bitmap> lruCache;
    static Context mContext;
//    private int width = 0;
//    private int height = 0;
//    ImageView mImageView;
//    HashMap<String, ImageView> mImageViewMap = new HashMap<>();
//    ExecutorService cachedThreadPool = Executors.newFixedThreadPool(8);

    private BitmapPool() {
    }

    private static BitmapPool bitmapPool;

    public static BitmapPool getInstance() {
        if (bitmapPool == null) {
            bitmapPool = new BitmapPool();
            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            int cacheSize = maxMemory / 8;
            lruCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
                }

                @Override
                protected void entryRemoved(boolean evicted, @NonNull String key, @NonNull Bitmap oldValue, @Nullable Bitmap newValue) {
                    removeImageCache(key);
                    super.entryRemoved(evicted, key, oldValue, newValue);
                }
            };
        }
        return bitmapPool;
    }

    public Bitmap getBitmap(String path) {
        Bitmap bitmap = lruCache.get(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        if (bitmap == null) {
            Bitmap bitmap1 = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Uri imageContentUri = getImageContentUri(path);
                try {
                    bitmap1 = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(imageContentUri), null, options);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                bitmap1 = BitmapFactory.decodeFile(path, options);
            }

            Matrix matrix = new Matrix();
            matrix.setScale(0.5f, 0.5f);
            Bitmap bitmap2 = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix, true);
            lruCache.put(path, bitmap2);
            return bitmap2;
        }
        return bitmap;
    }

    public static Uri getImageContentUri(String path) {
        if (path.endsWith("mp4")) {
            Cursor cursor = mContext.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Video.Media._ID}, MediaStore.Video.Media.DATA + "=? ",
                    new String[]{path}, null);
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/video/media");
                return Uri.withAppendedPath(baseUri, "" + id);
            }
        } else {
            Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                    new String[]{path}, null);
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                return Uri.withAppendedPath(baseUri, "" + id);
            }
        }
        return null;
    }

    public static void removeImageCache(String key) {
        if (key != null) {
            if (lruCache != null) {
                Bitmap bm = lruCache.remove(key);
                if (bm != null)
                    bm.recycle();
            }
        }
    }

    public void onDestroy() {
        if (lruCache != null && lruCache.size() > 0) {
            lruCache.evictAll();
            lruCache = null;
        }
    }

    public BitmapPool with(Context context) {
        mContext = context;
        return this;
    }

    public void into(ImageView imageView, String path) {
        imageView.setImageBitmap(getBitmap(path));
    }

//    public void into(ImageView imageView, String path) {
//        this.mImageView = imageView;
//        if (imageView != null && (width == 0 || height == 0)) {
//            imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    if (width == 0 || height == 0) {
//                        width = imageView.getMeasuredWidth();
//                        height = imageView.getMeasuredHeight();
//                        imageView.setImageBitmap(getBitmap(path));
//                    }
//                }
//            });
//        } else {
//            imageView.setImageBitmap(getBitmap(path));
//        }
//    }
}
