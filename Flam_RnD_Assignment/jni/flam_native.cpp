#include <jni.h>
#include <vector>
#include <android/log.h>

#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>

#define LOG_TAG "FlamNative"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_example_flamrnd_MainActivity_processFrame(
        JNIEnv *env,
        jobject /* this */,
        jbyteArray frame,
        jint width,
        jint height) {

    // NOTE: Right now this native method expects a single-channel (grayscale) image
    // coming from the Y plane of a YUV_420_888 frame.
    // It applies Canny edge detection and returns the result as a grayscale byte array.

    jsize len = env->GetArrayLength(frame);
    if (len <= 0 || width <= 0 || height <= 0) {
        return frame;
    }

    jboolean isCopy = JNI_FALSE;
    jbyte *inputBytes = env->GetByteArrayElements(frame, &isCopy);
    if (!inputBytes) {
        LOGE("Failed to get byte array elements");
        return frame;
    }

    // Wrap input bytes as OpenCV Mat (grayscale)
    cv::Mat gray(height, width, CV_8UC1, reinterpret_cast<unsigned char *>(inputBytes));
    cv::Mat edges;

    try {
        // Slight blur to reduce noise, then Canny
        cv::GaussianBlur(gray, gray, cv::Size(3, 3), 0);
        cv::Canny(gray, edges, 50, 150);
    } catch (const cv::Exception &e) {
        LOGE("OpenCV error: %s", e.what());
        // In case of error, just return original frame
        env->ReleaseByteArrayElements(frame, inputBytes, 0);
        return frame;
    }

    // Create output array and copy edges into it
    jbyteArray output = env->NewByteArray(len);
    if (!output) {
        env->ReleaseByteArrayElements(frame, inputBytes, 0);
        return frame;
    }

    env->SetByteArrayRegion(
            output,
            0,
            len,
            reinterpret_cast<jbyte *>(edges.data)
    );

    env->ReleaseByteArrayElements(frame, inputBytes, 0);
    return output;
}
