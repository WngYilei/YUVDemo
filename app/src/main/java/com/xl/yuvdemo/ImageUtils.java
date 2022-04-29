package com.xl.yuvdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.guo.android_extend.image.ImageConverter;
import com.xl.yuvdemo.bean.ImageBean;

/**
 * @Author : wyl
 * @Date : 2022/4/28
 * Desc :
 */
public class ImageUtils {
    public static ImageBean imgToNV21(String imagePath) {
        Bitmap bmp = null;
        int height = 0;
        int widght = 0;
        try {
            bmp = BitmapFactory.decodeFile(imagePath);
            height = bmp.getHeight();
            widght = bmp.getWidth();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();//bitmap容易出现内存泄漏，加个异常捕获
        }
        if (bmp == null) {
            Log.d("MainActivity", "Bitmap error");
        }

        byte[] mImageNV21 = new byte[bmp.getWidth() * bmp.getHeight() * 3 / 2];
        ImageConverter convert = new ImageConverter();
        try {
            convert.initial(bmp.getWidth(), bmp.getHeight(), ImageConverter.CP_PAF_NV21);
            if (convert.convert(bmp, mImageNV21)) {
                Log.d("MainActivity", "convert ok!");
            } else {
                Log.d("MainActivity", "convert error!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //回收内存
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
                bmp = null;
            }
            convert.destroy();
            System.gc();
        }
        ImageBean imageBean = new ImageBean(height, widght, mImageNV21);
        return imageBean;
    }

    public static Bitmap cover(byte[] data, int wight, int height) {

        byte[] dst = new byte[data.length];

        YuvUtils.convertNV21ToI420(data, dst, wight, height);

        byte[] dst2 = new byte[data.length];
        YuvUtils.compressI420(dst, wight, height, dst2, wight, height, 180, true);


        Bitmap bitmap = Bitmap.createBitmap(wight, height, Bitmap.Config.ARGB_8888);
        YuvUtils.convertI420ToBitmap(dst2, bitmap, wight, height);
        return bitmap;
    }
}
