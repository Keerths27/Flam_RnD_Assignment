# Flam RnD Assignment – Android + JNI + OpenGL + Web (Skeleton)

This repository is a **clean, minimal skeleton** for the Flam Software Engineering Intern (R&D) assignment.

It shows the end-to-end flow expected in the problem statement:

- Android app (Kotlin) with a `GLSurfaceView`
- JNI bridge to native C++ (`flam_native.cpp`)
- Placeholder for OpenCV-based frame processing in native code
- Simple OpenGL ES 2.0 renderer drawing a fullscreen quad
- TypeScript-based web viewer that displays basic stats about the processed stream

> NOTE: The native C++ currently **just echoes the input bytes**. You can extend it with real OpenCV logic if you have the SDK set up.

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

## How to extend (for bonus points)

- Replace the stub in `jni/flam_native.cpp` with real OpenCV code (Canny edges).
- Capture camera frames in `MainActivity` and send them to `processFrame(...)`.
- Bind the processed frame as a texture in `EdgeRenderer` instead of drawing an empty quad.
- Export one sample processed frame as PNG/JPEG and show it in the web viewer in place of the placeholder.
