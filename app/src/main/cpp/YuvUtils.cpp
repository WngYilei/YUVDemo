//
// Created by 王一蕾 on 2022/4/28.
//

#include "YuvUtils.h"

#include "libyuvdemo.h"


void YuvUtils::NV21ToI420(jbyte *src_nv21_data,jbyte *src_i420_data, jint width, jint height ) {
    jint src_y_size = width * height;
    jint src_u_size = (width >> 1) * (height >> 1);

    jbyte *src_nv21_y_data = src_nv21_data;
    jbyte *src_nv21_vu_data = src_nv21_data + src_y_size;

    jbyte *src_i420_y_data = src_i420_data;
    jbyte *src_i420_u_data = src_i420_data + src_y_size;
    jbyte *src_i420_v_data = src_i420_data + src_y_size + src_u_size;

    libyuv::NV21ToI420((const uint8_t *) src_nv21_y_data, width,
                       (const uint8_t *) src_nv21_vu_data, width,
                       (uint8_t *) src_i420_y_data, width,
                       (uint8_t *) src_i420_u_data, width >> 1,
                       (uint8_t *) src_i420_v_data, width >> 1,
                       width, height);

}

void YuvUtils::I420ToNV21(jbyte *src, jbyte *dst, int width, int height) {

    jint src_y_size = width * height;
    jint src_u_size = src_y_size >> 2;
    jbyte *src_y = src;
    jbyte *src_u = src + src_y_size;
    jbyte *src_v = src + src_y_size + src_u_size;

    jint dst_y_size = width * height;
    jbyte *dst_y = dst;
    jbyte *dst_uv = dst + dst_y_size;

    libyuv::I420ToNV12(
            (uint8_t *) src_y, width,
            (uint8_t *) src_u, width >> 1,
            (uint8_t *) src_v, width >> 1,
            (uint8_t *) dst_y, width,
            (uint8_t *) dst_uv, width,
            width, height
    );
}

void YuvUtils::I420ToABGR(jbyte *src, int width, int height, void *dst, int dst_stride) {
    jint src_y_size = width * height;
    jint src_u_size = src_y_size >> 2;
    jbyte *src_y = src;
    jbyte *src_u = src + src_y_size;
    jbyte *src_v = src + src_y_size + src_u_size;
    libyuv::I420ToABGR(
            (uint8_t *) src_y, width,
            (uint8_t *) src_u, width >> 1,
            (uint8_t *) src_v, width >> 1,
            (uint8_t *) dst, dst_stride,
            width, height
    );

}

void YuvUtils::I420Scale(jbyte *src, int src_width, int src_height, jbyte *dst,
                         int dst_width, int dst_height) {
    jint src_y_size = src_width * src_height;
    jint src_u_size = src_y_size >> 2;
    jbyte *src_y = src;
    jbyte *src_u = src + src_y_size;
    jbyte *src_v = src + src_y_size + src_u_size;

    jint dst_y_size = dst_width * dst_height;
    jint dst_u_size = dst_y_size >> 2;
    jbyte *dst_y = dst;
    jbyte *dst_u = dst + dst_y_size;
    jbyte *dst_v = dst + dst_y_size + dst_u_size;

    libyuv::I420Scale(
            (uint8_t *) src_y, src_width,
            (uint8_t *) src_u, src_width >> 1,
            (uint8_t *) src_v, src_width >> 1,
            src_width, src_height,
            (uint8_t *) dst_y, dst_width,
            (uint8_t *) dst_u, dst_width >> 1,
            (uint8_t *) dst_v, dst_width >> 1,
            dst_width, dst_height,
            libyuv::FilterMode::kFilterNone
    );

}

void YuvUtils::I420Rotate(jbyte *src, jbyte *dst, int &width, int &height,
                          int degree) {


    jint src_y_size = width * height;
    jint src_u_size = src_y_size >> 2;

    jbyte *src_y = src;
    jbyte *src_u = src + src_y_size;
    jbyte *src_v = src + src_y_size + src_u_size;

    jbyte *dst_y = dst;
    jbyte *dst_u = dst + src_y_size;
    jbyte *dst_v = dst + src_y_size + src_u_size;

    libyuv::I420Rotate(
            (uint8_t *) src_y, width,
            (uint8_t *) src_u, width >> 1,
            (uint8_t *) src_v, width >> 1,
            (uint8_t *) dst_y, height,
            (uint8_t *) dst_u, height >> 1,
            (uint8_t *) dst_v, height >> 1,
            width, height, (libyuv::RotationMode) degree
    );

    // 若为 90 / 270,  则翻转宽高
    if (degree == libyuv::kRotate90 || degree == libyuv::kRotate270) {
        width = width ^ height;
        height = width ^ height;
        width = width ^ height;
    }
}

void YuvUtils::I420Mirror(jbyte *src, jbyte *dst, int width, int height) {
    jint src_y_size = width * height;
    jint src_u_size = src_y_size >> 2;
    jbyte *src_y = src;
    jbyte *src_u = src + src_y_size;
    jbyte *src_v = src + src_y_size + src_u_size;

    jbyte *dst_y = dst;
    jbyte *dst_u = dst + src_y_size;
    jbyte *dst_v = dst + src_y_size + src_u_size;

    libyuv::I420Mirror((uint8_t *) src_y, width,
                       (uint8_t *) src_u, width >> 1,
                       (uint8_t *) src_v, width >> 1,
                       (uint8_t *) dst_y, width,
                       (uint8_t *) dst_u, width >> 1,
                       (uint8_t *) dst_v, width >> 1,
                       width, height);
}

void YuvUtils::I420Crop(jbyte *src, int src_width, int src_height, jbyte *dst, int dst_width,
                        int dst_height, int left, int top) {
    jint dst_y_size = dst_width * dst_height;
    jint dst_u_size = dst_y_size >> 2;
    jbyte *dst_y = dst;
    jbyte *dst_u = dst + dst_y_size;
    jbyte *dst_v = dst + dst_y_size + dst_u_size;

    libyuv::ConvertToI420(
            (uint8_t *) src, (size_t) src_width * src_height * 3 / 2,
            (uint8_t *) dst_y, dst_width,
            (uint8_t *) dst_u, dst_width >> 1,
            (uint8_t *) dst_v, dst_width >> 1,
            left, top,
            src_width, src_height,
            dst_width, dst_height,
            libyuv::kRotate0, libyuv::FOURCC_I420
    );

}


