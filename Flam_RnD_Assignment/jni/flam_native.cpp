#include <jni.h>
#include <vector>

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_example_flamrnd_MainActivity_processFrame(
        JNIEnv *env,
        jobject /* this */,
        jbyteArray frame,
        jint width,
        jint height) {

    // Minimal stub: just echoes the input bytes back.
    // In a full solution, you would:
    // - Convert NV21/YUV to grayscale or RGBA cv::Mat
    // - Apply cv::Canny or other edge detection using OpenCV
    // - Convert back to byte array and return

    jsize len = env->GetArrayLength(frame);
    jbyteArray output = env->NewByteArray(len);
    env->SetByteArrayRegion(output, 0, len, env->GetByteArrayElements(frame, nullptr));
    return output;
}
