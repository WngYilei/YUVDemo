//
// Created by 王一蕾 on 2022/5/4.
//

#ifndef YUVDEMO_CAMERAYUV_H
#define YUVDEMO_CAMERAYUV_H

#include <android/native_window_jni.h>

#include <pthread.h>


class CameraYuv {

public:
    CameraYuv();

    pthread_mutex_t mutex{};
    ANativeWindow *window = 0;

    void setSurfaceView(ANativeWindow *window);


    void detect(uint8_t *data, int widtg, int height);

    ~CameraYuv();
};


#endif //YUVDEMO_CAMERAYUV_H
