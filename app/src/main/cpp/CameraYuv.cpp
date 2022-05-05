//
// Created by 王一蕾 on 2022/5/4.
//

#include <cstring>
#include "CameraYuv.h"
#include "util.h"

#define ANATIVEWINDOW_RELEASE(window)   \
if (window) { \
            ANativeWindow_release(window); \
            window = 0; \
      }


CameraYuv::CameraYuv() = default;

CameraYuv::~CameraYuv() = default;

void CameraYuv::setSurfaceView(ANativeWindow *window) {
    pthread_mutex_lock(&mutex);
    ANATIVEWINDOW_RELEASE(this->window);
    this->window = window;
    pthread_mutex_unlock(&mutex);
}

void CameraYuv::detect(uint8_t *data, int width, int height) {
    pthread_mutex_lock(&mutex);

    do {
        if (!window) {
            return;
        }

        ANativeWindow_setBuffersGeometry(window, width, height, WINDOW_FORMAT_RGBA_8888);

        ANativeWindow_Buffer buffer;

        if (ANativeWindow_lock(window, &buffer, nullptr)) {
            ANATIVEWINDOW_RELEASE(this->window);
            break;
        }


        auto *dstData = static_cast<uint8_t *>(buffer.bits);
        int dstlineSize = buffer.stride * 4;
        uint8_t *srcData = data;
        int srclineSize = width * 4;


        for (int i = 0; i < buffer.height; ++i) {
            memcpy(dstData + i * dstlineSize, srcData + i * srclineSize, srclineSize);
        }


        LOGE("5");
        ANativeWindow_unlockAndPost(this->window);
    } while (false);

    pthread_mutex_unlock(&mutex);
}

