window.addEventListener("DOMContentLoaded", () => {
  const stats = document.getElementById("stats") as HTMLDivElement;
  const info = [
    "Resolution: 1280x720",
    "Approx FPS: 15",
    "Processing: Edge detection (Canny in native C++)"
  ];
  stats.innerText = info.join(" | ");
});
