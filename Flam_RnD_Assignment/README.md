# Flam RnD Assignment – Android + JNI + OpenGL + Web (Skeleton)

This repository is a **clean, minimal skeleton** for the Flam Software Engineering Intern (R&D) assignment.

It shows the end-to-end flow expected in the problem statement:

- Android app (Kotlin) with a `GLSurfaceView`
- JNI bridge to native C++ (`flam_native.cpp`)
- Placeholder for OpenCV-based frame processing in native code
- Simple OpenGL ES 2.0 renderer drawing a fullscreen quad
- TypeScript-based web viewer that displays basic stats about the processed stream

> NOTE: The native C++ currently **just echoes the input bytes**. You can extend it with real OpenCV logic if you have the SDK set up.

## My Implementation Notes
While working on this assignment, I focused on setting up the complete pipeline and structuring the project according to the requirements. The Android part is written in Kotlin and connected to native C++ through JNI. I added a simple native stub which is ready for OpenCV-based edge detection once the OpenCV SDK is linked.

The OpenGL ES 2.0 renderer is kept minimal for now. It draws a fullscreen quad and can later display textures once the processed frames are connected to it.

For the web part, I created a small TypeScript viewer to show placeholder stats and demonstrate how a processed frame preview would be displayed. I kept it simple so the focus remains on the native + Android pipeline.

## Structure

- `app/` – Android app module  
- `jni/` – Native C++ code + CMakeLists.txt  
- `gl/` – OpenGL ES 2.0 renderer (`EdgeRenderer.kt`)  
- `web/` – TypeScript web viewer (`index.ts` + `index.html`)

## Android – How to open

1. Open the root folder in **Android Studio**.  
2. Let Gradle sync.  
3. Make sure the NDK and CMake are installed (Android Studio will usually prompt).  
4. You may need to point `OpenCV_DIR` in `jni/CMakeLists.txt` if you add real OpenCV processing.  
5. Build and run on a physical device or emulator.

## Web Viewer

```bash
cd web
npm install
npm run build
npm run start
```

Then open the shown `http://localhost:...` URL. You will see:

- A placeholder box for the processed frame  
- Stats like resolution, FPS, and processing text at the bottom  

## Architecture Overview

### Android (Kotlin)
- Uses GLSurfaceView for rendering via OpenGL ES 2.0.
- Camera permission logic handled in MainActivity.kt.
- Prepared to capture camera frames and send them to JNI for processing.

### JNI Layer (C++)
- Kotlin calls the native function `processFrame(...)` in `flam_native.cpp`.
- JNI converts the byte array from Java/Kotlin into raw C++ memory.
- Current native stub echoes the frame back (placeholder).
- Can easily be extended with OpenCV (Canny/Sobel/ORB).

### OpenGL ES Renderer
- `EdgeRenderer.kt` draws a fullscreen quad using OpenGL ES 2.0.
- Renderer is ready for binding a texture once the native processed frame is available.
- This forms the rendering pipeline for visualizing processed images.

### Web Viewer (TypeScript)
- Serves a simple UI to preview processed frames.
- Displays a sample file (`processed_frame.png`) from the public directory.
- Shows placeholder stats (resolution, FPS, processing info).
- Can be extended with WebSockets to display live processed frames.

## How to extend (for bonus points)

- Replace the stub in `jni/flam_native.cpp` with real OpenCV code (Canny edges).  
- Capture camera frames in `MainActivity` and send them to `processFrame(...)`.  
- Bind the processed frame as a texture in `EdgeRenderer` instead of drawing an empty quad.  
- Export one sample processed frame as PNG/JPEG and show it in the web viewer.

## What I Would Do With More Time

- Implement full OpenCV integration on Android (Canny, Sobel, ORB features).  
- Capture live camera frames and stream them through JNI for real-time processing.  
- Replace the fullscreen quad with a proper texture-binding pipeline.  
- Add WebSocket communication so the web viewer updates automatically.  
- Improve UI/UX for both Android and Web.  
- Optimize native C++ code for better performance.

## Setup Instructions

### Android (NDK + CMake)

1. Open Android Studio.  
2. Go to **File → Settings → Appearance & Behavior → System Settings → Android SDK**.  
3. Open the **SDK Tools** tab.  
4. Install:  
   - **NDK (Side by Side)**  
   - **CMake**  
   - **LLDB** (optional)  
5. Click Apply → OK.  
6. Let Gradle sync and rebuild.

### OpenCV (Optional – for extending native C++)

If OpenCV integration is added later:

1. Download the OpenCV Android SDK:  
   https://opencv.org/releases/  
2. Extract the folder.  
3. In your `jni/CMakeLists.txt`, update:

```
set(OpenCV_DIR /path/to/OpenCV-android-sdk/sdk/native/jni)
find_package(OpenCV REQUIRED)
include_directories(${OpenCV_INCLUDE_DIRS})
```

4. Link OpenCV:

```
target_link_libraries(
    flam_native
    ${OpenCV_LIBS}
    log
)
```

## Screenshots

### Android App
![Android Screenshot](web/public/android_screen.png)

### Web Viewer
![Web Viewer Screenshot](web/public/web_screen.png)
