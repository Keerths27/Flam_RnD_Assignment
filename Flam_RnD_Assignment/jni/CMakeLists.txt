cmake_minimum_required(VERSION 3.10.2)
project("flam_native")

add_library(
    flam_native
    SHARED
    flam_native.cpp
)

# NOTE: You must point OpenCV_DIR to your OpenCV for Android SDK in Android Studio
# find_package(OpenCV REQUIRED)
# target_link_libraries(flam_native ${OpenCV_LIBS} log)

find_library(log-lib log)
target_link_libraries(flam_native ${log-lib})
