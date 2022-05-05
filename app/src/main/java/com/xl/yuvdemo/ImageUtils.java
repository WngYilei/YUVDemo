package com.xl.yuvdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;
import android.util.Log;

import androidx.camera.core.ImageProxy;

import com.guo.android_extend.image.ImageConverter;
import com.xl.yuvdemo.bean.ImageBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @Author : wyl
 * @Date : 2022/4/28
 * Desc :
 */
public class ImageUtils {
    public static ImageBean imgToNV21(Bitmap bitmap) {
        Bitmap bmp = null;
        int height = 0;
        int widght = 0;
        try {
            bmp = bitmap;
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

        YuvUtils.rotateI420(dst, wight, height, dst2, 180);

        Bitmap bitmap = Bitmap.createBitmap(wight, height, Bitmap.Config.ARGB_8888);
        YuvUtils.convertI420ToBitmap(dst2, bitmap, wight, height);

        return bitmap;
    }

    private static final String TAG = "ImageUtils";


    public static boolean areUVPlanesNV21(ImageProxy.PlaneProxy[] planes, int width, int height) {
        int imageSize = width * height;
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();
        // 备份缓冲区属性。
        int vBufferPosition = vBuffer.position();
        int uBufferLimit = uBuffer.limit();
        // 将 V 缓冲区推进 1 个字节，因为 U 缓冲区将不包含第一个 V 值。
        vBuffer.position(vBufferPosition + 1);
        // 切掉 U 缓冲区的最后一个字节，因为 V 缓冲区将不包含最后一个 U 值。
        uBuffer.limit(uBufferLimit - 1);
        // 检查缓冲区是否相等并具有预期的元素数量。
        boolean areNV21 = (vBuffer.remaining() == (2 * imageSize / 4 - 2)) && (vBuffer.compareTo(uBuffer) == 0);
        // 将缓冲区恢复到初始状态。
        vBuffer.position(vBufferPosition);
        uBuffer.limit(uBufferLimit);
        return areNV21;
    }



    public static ByteBuffer yuv420ThreePlanesToNV21(
            ImageProxy.PlaneProxy[] yuv420888planes, int width, int height) {
        int imageSize = width * height;
        byte[] out = new byte[imageSize + 2 * (imageSize / 4)];

        if (areUVPlanesNV21(yuv420888planes, width, height)) {
            // 复制 Y 的值
            yuv420888planes[0].getBuffer().get(out, 0, imageSize);
            // 从 V 缓冲区获取第一个 V 值，因为 U 缓冲区不包含它。
            yuv420888planes[2].getBuffer().get(out, imageSize, 1);
            // 从 U 缓冲区复制第一个 U 值和剩余的 VU 值。
            yuv420888planes[1].getBuffer().get(out, imageSize + 1, 2 * imageSize / 4 - 1);
        } else {
            // 回退到一个一个地复制 UV 值，这更慢但也有效。
            // 取 Y.
            unpackPlane(yuv420888planes[0], width, height, out, 0, 1);
            // 取 U.
            unpackPlane(yuv420888planes[1], width, height, out, imageSize + 1, 2);
            // 取 V.
            unpackPlane(yuv420888planes[2], width, height, out, imageSize, 2);
        }

        return ByteBuffer.wrap(out);
    }

    public static void unpackPlane(ImageProxy.PlaneProxy plane, int width, int height, byte[] out, int offset, int pixelStride) {
        ByteBuffer buffer = plane.getBuffer();
        buffer.rewind();

        // 计算当前平面的大小。假设它的纵横比与原始图像相同。
        int numRow = (buffer.limit() + plane.getRowStride() - 1) / plane.getRowStride();
        if (numRow == 0) {
            return;
        }
        int scaleFactor = height / numRow;
        int numCol = width / scaleFactor;

        // 提取输出缓冲区中的数据。
        int outputPos = offset;
        int rowStart = 0;
        for (int row = 0; row < numRow; row++) {
            int inputPos = rowStart;
            for (int col = 0; col < numCol; col++) {
                out[outputPos] = buffer.get(inputPos);
                outputPos += pixelStride;
                inputPos += plane.getPixelStride();
            }
            rowStart += plane.getRowStride();
        }
    }


    static byte[] data;
    static byte[] rowData;

    public static byte[] getDataFromImage(ImageProxy image) {
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        int size = width * height * ImageFormat.getBitsPerPixel(format) / 8;
        if (data == null || data.length != size) {
            data = new byte[size];
        }
        if (rowData == null || rowData.length != planes[0].getRowStride()){
            rowData = new byte[planes[0].getRowStride()];
        }
        int channelOffset = 0;  //偏移
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0; // y 从0开始
                    break;
                case 1:
                    channelOffset = width * height; // u 开始
                    break;
                case 2:
                    channelOffset = (int) (width * height * 1.25); // v开始 w*h+ w*h/4（u数据长度）
                    break;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride(); //行跨度 每行的数据量
            int pixelStride = planes[i].getPixelStride(); // 像素跨度 ,uv的存储间隔
            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift; // u与v只有一半
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            int length;
            for (int row = 0; row < h; row++) {
                if (pixelStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset++] = rowData[col * pixelStride];
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
        }
        return data;
    }




    public static String copyAsset2Dir(Context context, String name) {
        File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
        File cascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
        if (!cascadeFile.exists()) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = context.getAssets().open(name);
                fos = new FileOutputStream(cascadeFile);
                int len;
                byte[] buffer = new byte[2048];

                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is == null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos == null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return cascadeFile.getAbsolutePath();
    }


}
