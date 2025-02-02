package pl.zenit.cbb.renderer;

public class CameraViewport {
      public final Coords coords;
      public final Zoom zoom;

      public CameraViewport(Coords coords, Zoom zoom) {
            this.coords = coords;
            this.zoom = zoom;
      }
      
      public CameraViewport withCoords(Coords coords) {
            return new CameraViewport(coords, zoom);
      }

      public CameraViewport withZoom(Zoom zoom) {
            return new CameraViewport(coords, zoom);
      }
      
}
