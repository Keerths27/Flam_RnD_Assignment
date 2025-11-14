cmake_minimum_required(VERSION 3.10.2)
project("flam_native")

add_library(
    flam_native
    SHARED
    flam_native.cpp
)

find_library(log-lib log)

# NOTE: You must set OpenCV_DIR to point to your OpenCV for Android SDK
# in your local CMake or via Android Studio's CMake configuration.
# Example (in local.properties or environment):
#   -DOpenCV_DIR=/path/to/OpenCV-android-sdk/sdk/native/jni

find_package(OpenCV REQUIRED)

target_link_libraries(
    flam_native
    ${OpenCV_LIBS}
    ${log-lib}
)
