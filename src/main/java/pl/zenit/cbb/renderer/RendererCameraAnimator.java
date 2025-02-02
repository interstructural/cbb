package pl.zenit.cbb.renderer;

public strictfp class RendererCameraAnimator {

      private final Renderer renderer;
      private final double zoomFactor = 1.33f;
      private final double stepFactor = 30f;

      public RendererCameraAnimator(Renderer renderer) {
            this.renderer = renderer;
      }

      public void left() {
            double movage = stepFactor / renderer.getViewport().zoom.dpp;
            Coords moved = renderer.getViewport().coords;
            moved = new Coords(moved.x - movage, moved.y);
            renderer.setViewport(renderer.getViewport().withCoords(moved));
      }

      public void right() {
            double movage = stepFactor / renderer.getViewport().zoom.dpp;
            Coords moved = renderer.getViewport().coords;
            moved = new Coords(moved.x + movage, moved.y);
            renderer.setViewport(renderer.getViewport().withCoords(moved));
      }

      public void up() {
            double movage = stepFactor / renderer.getViewport().zoom.dpp;
            Coords moved = renderer.getViewport().coords;
            moved = new Coords(moved.x, moved.y - movage);
            renderer.setViewport(renderer.getViewport().withCoords(moved));            
      }

      public void down() {
            double movage = stepFactor / renderer.getViewport().zoom.dpp;
            Coords moved = renderer.getViewport().coords;
            moved = new Coords(moved.x, moved.y + movage);
            renderer.setViewport(renderer.getViewport().withCoords(moved));            
      }

      public void massiveIn() {
            renderer.setViewport(renderer.getViewport()
                    .withZoom(new Zoom(renderer.getViewport().zoom.dpp * (zoomFactor * 3d) )));
      }

      public void in() {
            //różnica centrów widoków
            double x = renderer.getViewport().coords.x;
            double xright = x + renderer.getGraphInfo().widthValues();
            double xmove = (x - xright)/2d + ((xright - x)/2d)/zoomFactor;
            double y = renderer.getViewport().coords.y;
            double ybottom = y + renderer.getGraphInfo().heightValues();
            double ymove = (y - ybottom)/2d + ((ybottom - y)/2d) /zoomFactor;
            
            Coords coords = new Coords(x - xmove, y - ymove);
            Zoom newZoom = new Zoom(renderer.getViewport().zoom.dpp * zoomFactor);
            renderer.setViewport(renderer.getViewport()
                    .withZoom(newZoom)
                    .withCoords(coords)
            );
      }

      public void massiveOut() {
            renderer.setViewport(renderer.getViewport()
                    .withZoom(new Zoom(renderer.getViewport().zoom.dpp / (zoomFactor * 3d) )));
      }

      public void out() {
            double x = renderer.getViewport().coords.x;
            double xright = x + renderer.getGraphInfo().widthValues();
            double xmove = (x - xright)/2d + ((xright - x)/2d) *zoomFactor;
            double y = renderer.getViewport().coords.y;
            double ybottom = y + renderer.getGraphInfo().heightValues();
            double ymove = (y - ybottom)/2d + ((ybottom - y)/2d) *zoomFactor;
            
            Coords coords = new Coords(x - xmove, y - ymove);
            Zoom newZoom = new Zoom(renderer.getViewport().zoom.dpp / zoomFactor);
            renderer.setViewport(renderer.getViewport()
                    .withZoom(newZoom)
                    .withCoords(coords));
      }

      public void centerAtZeroZero() {
            double width = renderer.getGraphInfo().widthValues();
            double height = renderer.getGraphInfo().heightValues();
            renderer.setViewport(renderer.getViewport().withCoords(
                    new Coords(-width/2, -height/2)
            ));
      }

      public void resetZoom() {
            renderer.setViewport(renderer.getViewport().withZoom(Zoom.DEFAULT));
      }

      
}
//---------------------------------------------------------------------------------------------------
/*
             100
          +-------+   
      100 |       | 500
          +-------+  
             200

*/