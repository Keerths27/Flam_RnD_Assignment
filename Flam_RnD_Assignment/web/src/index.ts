window.addEventListener("DOMContentLoaded", () => {
  const stats = document.getElementById("stats") as HTMLDivElement;

  const info = [
    "Sample processed frame: processed_frame.png",
    "Resolution: 640x480 (grayscale)",
    "Processing: Canny edge detection in native C++ using OpenCV"
  ];

  stats.innerText = info.join(" | ");
});
