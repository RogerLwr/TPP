package com.tianpingpai.utils;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.tianpingpai.core.ContextProvider;

import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {

    public static boolean resizeImageToPath(Uri src, String path, int tw, int th) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            String srcPath = getRealPathFromURI(src);
            BitmapFactory.decodeFile(srcPath, options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = options.outHeight / th;
            Bitmap b = BitmapFactory.decodeFile(srcPath, options);
            FileOutputStream fos = new FileOutputStream(path);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            b.recycle();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = { MediaStore.Images.Media.DATA };
            cursor = ContextProvider.getContext().getContentResolver().query(contentUri,  projection, null, null, null);
            if(cursor == null){
                return contentUri.getPath();
            }
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
