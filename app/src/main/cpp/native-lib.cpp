#include "YuvUtils.h"
#include "util.h"
#include "CameraYuv.h"
#include <jni.h>
#include <android/bitmap.h>
#include <cstring>


extern "C"
JNIEXPORT void JNICALL
Java_com_xl_yuvdemo_YuvUtils_convertNV21ToI420(JNIEnv *env, jclass clazz, jbyteArray i420_src,
                                               jbyteArray nv12_dst, jint width, jint height) {
    jbyte *src = env->GetByteArrayElements(i420_src, nullptr);
    jbyte *dst = env->GetByteArrayElements(nv12_dst, nullptr);

    YuvUtils::NV21ToI420(src, dst, width, height);

    env->ReleaseByteArrayElements(i420_src, src, 0);
    env->ReleaseByteArrayElements(nv12_dst, dst, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xl_yuvdemo_YuvUtils_compressI420(JNIEnv *env, jclass clazz, jbyteArray i420_src,
                                          jint src_width,
                                          jint src_height, jbyteArray i420_dst, jint dst_width,
                                          jint dst_height, jint degree, jboolean is_mirror) {

    jbyte *src = env->GetByteArrayElements(i420_src, nullptr);

    const int dst_size = src_height * src_width * 3 / 2;

    jbyte *scaled = src;
    if (src_width != dst_width || src_height != dst_height) {
        scaled = new jbyte[dst_size];
        YuvUtils::I420Scale(src, src_width, src_height, scaled, dst_width,
                            dst_height);
    }

    jbyte *rotated = scaled;
    if (degree != 0) {
        rotated = new jbyte[dst_size];
        YuvUtils::I420Rotate(scaled, rotated, dst_width, dst_height, degree);
        if (scaled != src) {
            delete[]scaled;
        }
    }

    jbyte *mirrored = rotated;
    if (is_mirror) {
        mirrored = new jbyte[dst_size];
        YuvUtils::I420Mirror(rotated, mirrored, dst_width, dst_height);
        if (rotated != src) {
            delete[]rotated;
        }
    }

    jbyte *dst = env->GetByteArrayElements(i420_dst, nullptr);
    memcpy(dst, mirrored, (size_t) dst_size);

    if (mirrored != src) {
        delete[]mirrored;
    }

    env->ReleaseByteArrayElements(i420_src, src, 0);
    env->ReleaseByteArrayElements(i420_dst, dst, 0);

}
extern "C"
JNIEXPORT void JNICALL
Java_com_xl_yuvdemo_YuvUtils_convertI420ToBitmap(JNIEnv *env, jclass clazz, jbyteArray i420_src,
                                                 jobject bitmap, jint width, jint height) {


    jbyte *src = env->GetByteArrayElements(i420_src, nullptr);
    void *dst_argb;
    AndroidBitmap_lockPixels(env, bitmap, &dst_argb);
    AndroidBitmapInfo info;
    AndroidBitmap_getInfo(env, bitmap, &info);

    if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        YuvUtils::I420ToABGR(src, width, height, dst_argb, info.stride);
    } else {

    }

    AndroidBitmap_unlockPixels(env, bitmap);
    env->ReleaseByteArrayElements(i420_src, src, 0);

}
extern "C"
JNIEXPORT void JNICALL
Java_com_xl_yuvdemo_YuvUtils_convertI420ToNV12(JNIEnv *env, jclass clazz, jbyteArray i420_src,
                                               jbyteArray nv21_dst, jint width, jint height) {

    jbyte *src = env->GetByteArrayElements(i420_src, nullptr);
    jbyte *dst = env->GetByteArrayElements(nv21_dst, nullptr);

    YuvUtils::I420ToNV21(src, dst, width, height);

    env->ReleaseByteArrayElements(i420_src, src, 0);
    env->ReleaseByteArrayElements(nv21_dst, dst, 0);
}




extern "C"
JNIEXPORT void JNICALL
Java_com_xl_yuvdemo_YuvUtils_mirrorI420(JNIEnv *env, jclass clazz, jbyteArray i420_src,
                                        jint src_width,
                                        jint src_height, jbyteArray i420_dst) {

    jbyte *src = env->GetByteArrayElements(i420_src, nullptr);
    jbyte *dst = env->GetByteArrayElements(i420_dst, nullptr);

    YuvUtils::I420Mirror(src, dst, src_width, src_height);

    env->ReleaseByteArrayElements(i420_src, src, 0);
    env->ReleaseByteArrayElements(i420_dst, dst, 0);

}
extern "C"
JNIEXPORT void JNICALL
Java_com_xl_yuvdemo_YuvUtils_rotateI420(JNIEnv *env, jclass clazz, jbyteArray i420_src,
                                        jint src_width,
                                        jint src_height, jbyteArray i420_dst, jint degree) {

    jbyte *src = env->GetByteArrayElements(i420_src, nullptr);
    jbyte *dst = env->GetByteArrayElements(i420_dst, nullptr);

    YuvUtils::I420Rotate(src, dst, src_width, src_height, degree);

    env->ReleaseByteArrayElements(i420_src, src, 0);
    env->ReleaseByteArrayElements(i420_dst, dst, 0);

}
extern "C"
JNIEXPORT void JNICALL
Java_com_xl_yuvdemo_YuvUtils_scaledI420(JNIEnv *env, jclass clazz, jbyteArray i420_src,
                                        jint src_width,
                                        jint src_height, jbyteArray i420_dst, jint dst_width,
                                        jint dst_height) {

    jbyte *src = env->GetByteArrayElements(i420_src, nullptr);
    jbyte *dst = env->GetByteArrayElements(i420_dst, nullptr);

    YuvUtils::I420Scale(src, src_width, src_height, dst, dst_width, dst_height);

    env->ReleaseByteArrayElements(i420_src, src, 0);
    env->ReleaseByteArrayElements(i420_dst, dst, 0);
}


extern "C"
JNIEXPORT jlong JNICALL
Java_com_xl_yuvdemo_YuvUtils_setSurfaceView(JNIEnv *env, jclass clazz, jobject surface_view) {
    auto *cameraYuv = new CameraYuv();
    if (!surface_view) {
        cameraYuv->setSurfaceView(nullptr);
        return 0;
    }
    cameraYuv->setSurfaceView(ANativeWindow_fromSurface(env, surface_view));
    return (jlong) cameraYuv;
}



extern "C"
JNIEXPORT void JNICALL
Java_com_xl_yuvdemo_YuvUtils_nativeDetect(JNIEnv *env, jclass clazz, jlong native_camera_yuv,
                                          jbyteArray data, jint width, jint height,
                                          jint rotation_degrees) {

    if (!native_camera_yuv) return;

    auto *cameraYuv = reinterpret_cast<CameraYuv *>(native_camera_yuv);
    jbyte *input_image = env->GetByteArrayElements(data, nullptr);

    cameraYuv->detect((uint8_t *) input_image, width, height);

}



extern "C"
JNIEXPORT void JNICALL
Java_com_xl_yuvdemo_YuvUtils_release(JNIEnv *env, jclass clazz) {


}

