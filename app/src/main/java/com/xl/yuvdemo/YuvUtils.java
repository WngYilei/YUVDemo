package com.xl.yuvdemo;

import android.graphics.Bitmap;
import android.view.Surface;

/**
 * @Author : wyl
 * @Date : 2022/4/27
 * Desc :
 */
public class YuvUtils {

    private static long nativeWindow = 0;

    static {
        System.loadLibrary("yuvdemo");
    }

    public static void setSurface(Surface surfaceView) {
        nativeWindow = setSurfaceView(surfaceView);
    }

    public static void detect(byte[] data, int width, int height, int rotationDegrees) {
        nativeDetect(nativeWindow, data, width, height, rotationDegrees);
    }

    /**
     * 将 NV21 转 I420
     */
    public static native void convertNV21ToI420(byte[] src, byte[] dst, int width, int height);

    /**
     * 压缩 I420 数据
     * <p>
     * 执行顺序为:缩放->旋转->镜像
     *
     * @param src       原始数据
     * @param srcWidth  原始宽度
     * @param srcHeight 原始高度
     * @param dst       输出数据
     * @param dstWidth  输出宽度
     * @param dstHeight 输出高度
     * @param degree    旋转(90, 180, 270)
     * @param isMirror  镜像(镜像在旋转之后)
     */
    public static native void compressI420(byte[] src, int srcWidth, int srcHeight,
                                           byte[] dst, int dstWidth, int dstHeight,
                                           int degree, boolean isMirror);

    /**
     * 将 I420 数据注入到 Bitmap 中
     */
    public static native void convertI420ToBitmap(byte[] src, Bitmap dst, int width, int height);

    /**
     * 将 I420 转 NV12
     */
    public static native void convertI420ToNV12(byte[] src, byte[] dst, int width, int height);


    public static native void mirrorI420(byte[] src, int srcWidth, int srcHeight, byte[] dst);

    public static native void rotateI420(byte[] src, int srcWidth, int srcHeight, byte[] dst, int degree);


    public static native void scaledI420(byte[] src, int srcWidth, int srcHeight, byte[] dst, int dstWidth, int dstHeight);


    public static native long setSurfaceView(Surface surfaceView);


    public static native void nativeDetect(long nativeWindow, byte[] data, int widtg, int height, int rotationDegrees);


    public static native void release();
}
