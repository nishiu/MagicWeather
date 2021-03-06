package com.frostsowner.magic.weather.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liyu on 2016/11/17.
 */

public class RxImage {

    public static Observable<Uri> saveImageAndGetPathObservable(final Activity context, final String url, final String title) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Bitmap bitmap = null;
                try {
                    bitmap = Glide.with(context)
                            .load(url)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                if (bitmap == null) {
                    subscriber.onError(new Exception("无法下载到图片!"));
                }
                subscriber.onNext(bitmap);
                subscriber.onCompleted();
            }
        })
        .flatMap(new Func1<Bitmap, Observable<Uri>>() {
            @Override
            public Observable<Uri> call(Bitmap bitmap) {
                File appDir = new File(Environment.getExternalStorageDirectory(), "weather_share");
                if (!appDir.exists()) {
                    appDir.mkdir();
                }
                String fileName = title.replace('/', '-') + ".jpg";
                File file = new File(appDir, fileName);
                try {
                    FileOutputStream outputStream = new FileOutputStream(file);
                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri uri = Uri.fromFile(file);
                Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                context.sendBroadcast(scannerIntent);
                Uri contentURI = getImageContentUri(context, file.getAbsolutePath());
                return Observable.just(contentURI);
            }
        }).subscribeOn(Schedulers.io());

    }

    public static Observable<Uri> saveText2ImageObservable(final Activity context, final CardView view) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
                    @Override
                    public void call(Subscriber<? super Bitmap> subscriber) {

                        Bitmap bitmap = null;
                        try {
                            bitmap = saveViewToBitmap(view);
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                        if (bitmap == null) {
                            subscriber.onError(new Exception("无法生产图片!"));
                        }
                        subscriber.onNext(bitmap);
                        subscriber.onCompleted();
                    }
                })
                .flatMap(new Func1<Bitmap, Observable<Uri>>() {
                    @Override
                    public Observable<Uri> call(Bitmap bitmap) {
                        File appDir = new File(Environment.getExternalStorageDirectory(), "Girls");
                        if (!appDir.exists()) {
                            appDir.mkdir();
                        }
                        String fileName = "share.jpg";
                        File file = new File(appDir, fileName);
                        try {
                            FileOutputStream outputStream = new FileOutputStream(file);
                            assert bitmap != null;
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Uri uri = Uri.fromFile(file);
                        Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                        context.sendBroadcast(scannerIntent);
                        Uri contentURI = getImageContentUri(context, file.getAbsolutePath());
                        return Observable.just(contentURI);
                    }
                }).subscribeOn(Schedulers.io());

    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static Uri getImageContentUri(Context context, String absPath) {

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , new String[]{MediaStore.Images.Media._ID}
                , MediaStore.Images.Media.DATA + "=? "
                , new String[]{absPath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(id));

        } else if (!absPath.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, absPath);
            return context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            return null;
        }
    }

    private static Bitmap saveViewToBitmap(ViewGroup cardView) {
        int h = 0;
        for (int i = 0; i < cardView.getChildCount(); i++) {
            h += cardView.getChildAt(i).getHeight();
        }
        Bitmap bitmap = Bitmap.createBitmap(cardView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        cardView.draw(canvas);
        return bitmap;
    }

}
