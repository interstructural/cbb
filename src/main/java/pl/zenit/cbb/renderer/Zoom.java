package pl.zenit.cbb.renderer;

public class Zoom {

      public static final Zoom DEFAULT = new Zoom(100);
      
      public final double dpp;

      public Zoom(double dpi) {
            this.dpp = dpi;
      }
      
}
